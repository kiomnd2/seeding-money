package com.kakaopay.seedingmoeny.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SeedingResponse<T> {

    final private String code;

    final private String message;

    final private T body;

}
