package com.kakaopay.seedingmoeny.exception;

public class ExpiredSearchDateException extends RuntimeException {
    public ExpiredSearchDateException() {
        super("조회할 수 있는 기간이 지났습니다.");
    }
}
