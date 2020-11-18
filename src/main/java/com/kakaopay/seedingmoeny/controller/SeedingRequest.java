package com.kakaopay.seedingmoeny.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SeedingRequest {

    /**
     * 뿌리기 요청 금액
     */
    @NotNull
    final private BigDecimal amount;

    /**
     * 받을 수 있는 사람 수
     */
    @NotNull
    final private int receiverNumber;
}
