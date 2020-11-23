package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.exception.DuplicateCropsException;
import com.kakaopay.seedingmoeny.exception.ExpiredCropsException;
import com.kakaopay.seedingmoeny.exception.NothingMoneyException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@SpringBootTest
@ActiveProfiles("test")
class CropsServiceTest {

    @Autowired
    SeedingSessionService seedingSessionService;

    @Autowired
    SeedingService seedingService;

    @Autowired
    CropsService cropsService;

    @Autowired
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;

    @DisplayName("뿌리기한 금액을 받고, 제대로 받았는지 체크합니다.")
    @Test
    void harvestingTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        Seeding seeding = seedingService.seeding(userId, roomId, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        Crops harvesting = cropsService.harvesting(seeding, userId);

        assertThat(harvesting.getReceiveUserId()).isEqualTo(userId);
        assertThat(harvesting.isReceived()).isEqualTo(true);
        assertThat(harvesting.getReceiveAmount()).isGreaterThan(BigDecimal.ZERO);
    }


    @DisplayName("뿌리기당 한사용자는 한번만 받을 수 있습니다 ( 같은 아이디 중복 요청 실패 )")
    @Test
    void harvestingDuplicateCropsExceptionTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        Seeding seeding = seedingService.seeding(userId, roomId, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        cropsService.harvesting(seeding, userId);

        // 같은 아이디가 중복으로요창
        assertThat( assertThatExceptionOfType(DuplicateCropsException.class).isThrownBy(() ->
                cropsService.harvesting(seeding, userId)));
    }


    @DisplayName("남은 돈 없을 경우")
    @Test
    void harvestingNothingMoneyExceptionTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        Seeding seeding = seedingService.seeding(userId, roomId, seedingRequest);

        cropsService.harvesting(seeding, userId);

        long user2 = 222;

        long user3 = 333;

        cropsService.harvesting(seeding, user2);

        // 마감 체크
        assertThat( assertThatExceptionOfType(NothingMoneyException.class).isThrownBy(() ->
                cropsService.harvesting(seeding, user3)));
    }

    @DisplayName("뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다")
    @Test
    void harvestingExpireCropsExceptionTest() {
        String roomId ="123";
        String token = "asd";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);


        // 20 분  데이터를 저장
        Seeding seeding = seedingRepository.save(Seeding.builder()
                .token(token)
                .userId(userId)
                .amount(amount)
                .seedingSession(seedingSession)
                .seedingAt(LocalDateTime.now().minusMinutes(20))
                .status(SeedingStatus.CREATED)
                .build());

        cropsService.divideCrops(seedingRequest, seeding);

        long user = 222;

        // 20 분 이후에 요청
        assertThat( assertThatExceptionOfType(ExpiredCropsException.class).isThrownBy(() ->
                seedingService.checkSeeding(user, seedingSession, token)));
    }





}
