package com.kakaopay.seedingmoeny.exception;

public class DuplicateCropsException extends RuntimeException{
    public DuplicateCropsException() {
        super("두번 요청 할 수 없습니다!");
    }
}
