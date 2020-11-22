package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.exception.SelfCropsMoneyException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.util.TokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.kakaopay.seedingmoeny.util.TokenGenerator.createToken;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SeedingServiceTest {

    @Autowired
    SeedingService seedingService;

    @Autowired
    CropsService cropsService;

    @Autowired
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;

    @Autowired
    SeedingSessionService seedingSessionService;

    @Test
    void seedingTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        String token = seeding.getToken();

        Seeding result = seedingRepository.findByUserIdAndTokenAndSeedingSession(userId, token, seedingSession).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getSeedingAt()).isEqualTo(seeding.getSeedingAt());
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(seedingSession.getRoomId()).isEqualTo(roomId);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @DisplayName("뿌릴 금액을 인원수에 맞게 분배하여 저장합니다.")
    @Test
    void divideCropsTest() {

        long userId =333;
        int receiverNumber = 3;
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        // 토큰 생성

        String token = createToken();
        // 세션 생성
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 뿌리기정보 생성 후 저장
        Seeding seeding = Seeding.builder()
                .token(token)
                .amount(amount)
                .userId(userId)
                .seedingSession(seedingSession)
                .status(SeedingStatus.CREATED)
                .seedingAt(LocalDateTime.now())
                .build();

        Seeding persistedSeeding = seedingRepository.save(seeding);

        // 분배하여 저장
        cropsService.divideCrops(seedingRequest, persistedSeeding);

        List<Crops> cropsList = cropsRepository.findAllBySeeding(persistedSeeding);

        assertThat(cropsList.size()).isEqualTo(receiverNumber); // 3등분 됬는지
        assertThat(cropsList.get(0).isReceived()).isFalse();
        assertThat(cropsList.get(0).getReceiveUserId()).isEqualTo(0L);
        assertThat(cropsList.get(0).getReceiveAmount()).isGreaterThan(BigDecimal.ZERO);
    }


    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다.")
    @Test
    void checkSeedingSelfCropsExceptionTest() throws Exception {

        long userId =333;
        int receiverNumber = 3;
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        // 토큰 생성

        String token = createToken();
        // 세션 생성
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 뿌리기 정보 생성
        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        // 체크
        // 자기가 뿌린돈은 자기가 받을 수 없다
        assertThatExceptionOfType(SelfCropsMoneyException.class).isThrownBy(() ->
         seedingService.checkSeeding(seeding.getUserId(), seeding.getSeedingSession(), seeding.getToken()));
    }

    @DisplayName("뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.")
    @Test
    void checkSeedingExceptionTest() throws Exception {

        long userId =333;
        int receiverNumber = 3;
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        // 토큰 생성

        String token = createToken();
        // 세션 생성
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 뿌리기 정보 생성
        seedingService.seeding(userId, seedingSession, seedingRequest);


        // 다른 유저, 이상한 세션정보
        String _roomId = "124";
        SeedingSession _seedingSession = seedingSessionService.createSeedingSession(_roomId);

        assertThat( assertThatExceptionOfType(InvalidAccessException.class).isThrownBy(() ->
                seedingService.checkSeeding(111, _seedingSession, token)));
    }


    @DisplayName("정합성이 판단된 토큰인지 체크합니다.")
    @Test
    void checkSeedingInvalidTokenExceptionTest() throws Exception {

        long userId =333;
        int receiverNumber = 3;
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        // 토큰 생성

        String token = createToken();
        // 세션 생성
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 뿌리기 정보 생성
        seedingService.seeding(userId, seedingSession, seedingRequest);


        // 다른 유저, 이상한 세션정보
        String _roomId = "124";
        SeedingSession _seedingSession = seedingSessionService.createSeedingSession(_roomId);

        // 다른 유저, 이상한 토큰값
        assertThat( assertThatExceptionOfType(InvalidAccessException.class).isThrownBy(() ->
                seedingService.checkSeeding(111, _seedingSession, token)));
    }



}
