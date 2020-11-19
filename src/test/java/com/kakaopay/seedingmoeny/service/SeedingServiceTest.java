package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.repository.TokenRepository;
import org.assertj.core.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SeedingServiceTest {

    @Autowired
    SeedingService seedingService;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;

    @Autowired
    TokenService tokenService;

    @Test
    void seedingTest() {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId =333;
        int receiverNumber = 3;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);
        SeedingDto seeding = seedingService.seeding(roomId, userId, seedingRequest);

        Token token = tokenRepository.findById(seeding.getToken()).orElse(null);
        Optional<Seeding> byTokenAndRoomId = seedingRepository.findByTokenAndRoomId(token, roomId);

        Seeding result = byTokenAndRoomId.orElse(null);

        assertThat(result).isNotNull();
        assertThat(token).isNotNull();
        assertThat(result.getToken().getValue()).isEqualTo(token.getValue());
        assertThat(result.getToken().getIssuedAt()).isEqualTo(token.getIssuedAt());
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getRoomId()).isEqualTo(roomId);
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
        Token token = tokenService.createToken();

        // 뿌리기정보 생성 후 저장
        Seeding seeding = Seeding.builder()
                .token(token)
                .amount(amount)
                .userId(userId)
                .roomId(roomId).build();

        Seeding persistedSeeding = seedingRepository.save(seeding);

        // 분배하여 저장
        seedingService.divideCrops(seedingRequest, persistedSeeding);

        List<Crops> cropsList = cropsRepository.findAllBySeeding(persistedSeeding);

        assertThat(cropsList.size()).isEqualTo(receiverNumber); // 3등분 됬는지
        assertThat(cropsList.get(0).isReceived()).isFalse(); // 3등분 됬는지

    }
}
