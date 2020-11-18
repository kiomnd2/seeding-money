package com.kakaopay.seedingmoeny.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class SeedingDto {

    private final String token;

    private final LocalDateTime issuedAt;

}
