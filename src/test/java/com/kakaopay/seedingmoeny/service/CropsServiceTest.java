package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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


}
