package com.kakaopay.seedingmoeny.exception;

public class ExpiredCropsException extends RuntimeException {
    public ExpiredCropsException() {
        super("기한이 초과되었습니다");
    }
}
