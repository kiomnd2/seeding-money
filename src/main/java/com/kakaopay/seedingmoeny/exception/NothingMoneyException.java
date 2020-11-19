package com.kakaopay.seedingmoeny.exception;

public class NothingMoneyException extends RuntimeException{
    public NothingMoneyException() {
        super("마감되었습니다! 다음기회에!");
    }
}
