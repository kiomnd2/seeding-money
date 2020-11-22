package com.kakaopay.seedingmoeny.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class SeedingDto {

    /**
     * 토큰 값
     */
    private final String token;

    /**
     * 토큰 발행일
     */
    private final LocalDateTime issuedAt;

}
