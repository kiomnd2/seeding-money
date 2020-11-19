package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class SeedingService {

    final private TokenService tokenService;

    final private CropsRepository cropsRepository;

    final private SeedingRepository seedingRepository;

    final private MoneyDivideUtil divider;
    /**
     *
     * @param roomId : 방의 고유 아이디
     * @param userId : 사용자 아이디
     * @param request : 요청값 { amount, receiverNumber }
     * @return SeedingDto 토큰값과 발행일
     */
    @Transactional
    public SeedingDto seeding(String roomId, long userId, SeedingRequest request) {

        // 토큰 생성
        Token token = tokenService.createToken();

        // 뿌리기 정보 입력
        Seeding seeding = Seeding.builder()
                .userId(userId)
                .roomId(roomId)
                .amount(request.getAmount())
                .token(token).build();

        seedingRepository.save(seeding);

        // 금액을 나누어 저장
        divideCrops(request, seeding);

        return SeedingDto.builder().token(seeding.getToken().getValue())
                .issuedAt(seeding.getToken().getIssuedAt()).build();
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
                    .build();
            cropsRepository.save(crops);
        });
    }

}
