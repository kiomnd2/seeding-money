package com.kakaopay.seedingmoeny.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SeedingRequest {

    /**
     * 뿌리기 요청 금액
     */
    private final long amount;

    /**
     * 받을 수 있는 사람 수
     */
    private final int receiveNumber;
}
