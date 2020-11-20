package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.util.MoneyDivideUtil;
import com.kakaopay.seedingmoeny.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
@Service
public class SeedingService {

    private final TokenGenerator tokenGenerator;

    private final CropsRepository cropsRepository;

    private final SeedingRepository seedingRepository;

    private final SeedingSessionService seedingSessionService;

    private final MoneyDivideUtil divider;
    /**
     *
     * @param roomId : 방의 고유 아이디
     * @param userId : 사용자 아이디
     * @param request : 요청값 { amount, receiverNumber }
     * @return SeedingDto 토큰값과 발행일
     */
    @Transactional
    public SeedingDto seeding(String roomId, long userId, SeedingRequest request) {

        // 최초 세션 생성
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 토큰 생성
        String token = tokenGenerator.createToken();

        Seeding seeding = Seeding.builder()
                .token(token)
                .userId(userId)
                .amount(request.getAmount())
                .seedingSession(seedingSession)
                .seedingAt(LocalDateTime.now())
                .status(SeedingStatus.CREATED)
                .build();

        seedingRepository.save(seeding);

        // 금액을 나누어 저장
        divideCrops(request, seeding);

        return SeedingDto.builder().token(seeding.getToken())
                .issuedAt(seeding.getSeedingAt()).build();
    }

    /**
     * 금액을 나누어 저장합니다
     * @param request 요청값 { amount, receiverNumber }
     * @param seeding 뿌리기 정보
     */
    @Transactional
    public void divideCrops(SeedingRequest request, Seeding seeding) {
        // 금액을 나누어 저장
        divider.divide(request).forEach(i -> {
            Crops crops = Crops.builder()
                    .seeding(seeding)
                    .received(false)
                    .receiveAmount(i)
                    .harvestAt(LocalDateTime.now())
                    .build();
            cropsRepository.save(crops);
        });
    }

}
