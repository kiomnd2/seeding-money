package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.exception.*;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kakaopay.seedingmoeny.util.TokenGenerator.createToken;


@Slf4j
@RequiredArgsConstructor
@Service
public class SeedingService {

    private final SeedingRepository seedingRepository;

    private final CropsService cropsService;

    private final SeedingSessionService seedingSessionService;

    /**
     * 현재 세션에 뿌리기를 실행합니다
     * @param userId 사용자 아이디
     * @param roomId  현재 소속된 세션
     * @param request 요청 값 { amount ,requestNumber }
     * @return 뿌리기 정보
     */
    @Transactional
    public Seeding seeding(long userId, String roomId, SeedingRequest request) {

        // 최초 뿌리기 세션정보를 가져옴
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 토큰 생성
        String token = createToken();

        // 만약 같은 방에 같은 토큰이 생성되었을 경우 체크
        if (seedingRepository.existsBySeedingSessionAndToken(seedingSession, token)) {
            throw new RequestFailedException();
        }

        Seeding seeding = Seeding.builder()
                .token(token)
                .userId(userId)
                .amount(request.getAmount())
                .seedingSession(seedingSession)
                .seedingAt(LocalDateTime.now())
                .status(SeedingStatus.CREATED)
                .build();

        // 받을 금액 분배
        cropsService.divideCrops(request, seeding);

        return seedingRepository.save(seeding);
    }


    public Crops harvest(long userId, String roomId, String token) {
        // 사용자, 세션, 토큰에 대한 정합성검사
        SeedingSession seedingSession = seedingSessionService.getSeedingSession(roomId);

        // 정합성 검사
        Seeding seeding = checkSeeding(userId, seedingSession, token);

        // 받기 서비스 호출
        return cropsService.harvesting(seeding, userId);

    }

    /**
     * 조회 기준을 체크 합니다
     * @param userId 사용자 아이디
     * @param roomId 소속된 세션
     * @param token 토큰 값
     * @return 검증된 뿌리기 정보
     */
    public Seeding inquire(long userId, String roomId, String token) {

        SeedingSession seedingSession = seedingSessionService.getSeedingSession(roomId);

        Seeding seeding = getSeeding(seedingSession, token);

        if (!seeding.canSearch()) {
            throw new ExpiredSearchDateException();
        }

        if (!seeding.isOwner(userId)) {
            throw new InvalidAccessException();
        }

        return seeding;
    }

    /**
     * 뿌려진 돈을 줍기 전 현재 뿌려진 정보에 대한 정합성을 판단합니다
     * @param userId 사용자 아이디
     * @param seedingSession 소속 세션
     * @param token 토큰 값
     * @return 정합성이 검증된 뿌리기 정보
     */
    @Transactional(readOnly = true)
    protected Seeding checkSeeding(long userId, SeedingSession seedingSession, String token) {

        Seeding seeding = getSeeding(seedingSession, token);

        // 10분 초과인지 확인
        if (seeding.isExpired()) {
            throw new ExpiredCropsException();
        }

        if (seeding.isOwner(userId)) {
            throw new SelfCropsMoneyException();
        }

        return seeding;
    }

    /**
     * 뿌리기 정보를 가져옵니다.
     * @param seedingSession 소속 세션
     * @param token 토큰 값
     * @return 뿌리기 정보
     */
    private Seeding getSeeding(SeedingSession seedingSession, String token) {
        return seedingRepository.findByTokenAndSeedingSession(token, seedingSession)
                .orElseThrow(InvalidAccessException::new);
    }

}
