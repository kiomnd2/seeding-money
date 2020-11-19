package com.kakaopay.seedingmoeny.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class CropsDto {

    /**
     * 뿌린 돈을 수령한 사용자
     */
    private final long userId;

    /**
     * 뿌린 돈을 수령한 양
     */
    private final BigDecimal receiveAmount;

    /**
     * 돈을 수령한 일시
     */
    private final LocalDateTime pickedAt;
}
