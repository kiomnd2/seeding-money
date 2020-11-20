package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.exception.DuplicateCropsException;
import com.kakaopay.seedingmoeny.exception.ExpiredCropsException;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.exception.NothingMoneyException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;


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


    @Test
    void harvestingTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        Crops harvesting = cropsService.harvesting(seeding, userId);

        assertThat(harvesting.getReceiveUserId()).isEqualTo(userId);
        assertThat(harvesting.isReceived()).isEqualTo(true);
        assertThat(harvesting.getReceiveAmount()).isGreaterThan(BigDecimal.ONE);
    }


    @Test
    void harvestingDuplicateCropsExceptionTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        Crops harvesting = cropsService.harvesting(seeding, userId);

        // 같은 아이디가 중복으로요창
        assertThat( assertThatExceptionOfType(DuplicateCropsException.class).isThrownBy(() ->
                cropsService.harvesting(seeding, userId)));
    }


    @Test
    void harvestingNothingMoneyExceptionTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        cropsService.harvesting(seeding, userId);

        long user2 = 222;

        long user3 = 333;

        cropsService.harvesting(seeding, user2);

        // 같은 아이디가 중복으로요창
        assertThat( assertThatExceptionOfType(NothingMoneyException.class).isThrownBy(() ->
                cropsService.harvesting(seeding, user3)));
    }

    @Test
    void harvestingExpireCropsExceptionTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;
        String token = "ddd";

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
