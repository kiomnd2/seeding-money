package com.kakaopay.seedingmoeny.exception;

public class InvalidAccessException extends RuntimeException {
    public InvalidAccessException() {
        super("잘못된 접근입니다!");
    }
}
