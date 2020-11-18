package com.kakaopay.seedingmoeny.controller;

import com.kakaopay.seedingmoeny.code.Codes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SeedingResponse<T> {

    final private String code;

    final private String message;

    final private T body;


    public static <T> SeedingResponse<T> success (T body) {
        return new SeedingResponse<>(Codes.S0000.code, Codes.S0000.desc, body);
    }

    public static <T> SeedingResponse<T> fail (T body) {
        return new SeedingResponse<>(Codes.E2000.code, Codes.E2000.desc, body);
    }
}
