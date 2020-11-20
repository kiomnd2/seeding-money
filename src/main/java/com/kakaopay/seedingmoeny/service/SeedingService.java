package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.exception.SelfCropsMoneyException;
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

    private final SeedingRepository seedingRepository;

    private final CropsService cropsService;

    private final SeedingSessionService seedingSessionService;


    /**
     *
     * @return SeedingDto 토큰값과 발행일
     */
    @Transactional
    public Seeding seeding(long userId, SeedingSession seedingSession, SeedingRequest request) {

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

        return seedingRepository.save(seeding);
    }

    @Transactional(readOnly = true)
    public Seeding checkSeeding(long userId, SeedingSession seedingSession, String token) {

        Seeding seeding = seedingRepository.findByTokenAndSeedingSession(token, seedingSession)
                .orElseThrow(InvalidAccessException::new);

        if (seeding.isOwner(userId)) {
            throw new SelfCropsMoneyException();
        }
        return seeding;
    }

}
