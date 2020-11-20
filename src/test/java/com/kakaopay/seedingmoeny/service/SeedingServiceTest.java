package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.util.TokenGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeedingServiceTest {

    @Autowired
    SeedingService seedingService;

    @Autowired
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;

    @Autowired
    SeedingSessionService seedingSessionService;

    @Autowired
    TokenGenerator tokenGenerator;

    @Test
    void seedingTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);
        SeedingDto seeding = seedingService.seeding(roomId, userId, seedingRequest);

        String token = seeding.getToken();

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding result = seedingRepository.findByTokenAndSeedingSession(token, seedingSession).orElse(null);


        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.getSeedingAt()).isEqualTo(seeding.getIssuedAt());
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(seedingSession.getRoomId()).isEqualTo(roomId);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void divideCropsTest() {

        long userId =333;
        int receiverNumber = 3;
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        // 토큰 생성

        String token = tokenGenerator.createToken();

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
        seedingService.divideCrops(seedingRequest, persistedSeeding);

        List<Crops> cropsList = cropsRepository.findAllBySeeding(persistedSeeding);

        assertThat(cropsList.size()).isEqualTo(receiverNumber); // 3등분 됬는지
        assertThat(cropsList.get(0).isReceived()).isFalse(); // 3등분 됬는지

    }
}
