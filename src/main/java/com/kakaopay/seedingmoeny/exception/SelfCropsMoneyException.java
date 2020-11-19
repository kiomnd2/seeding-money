package com.kakaopay.seedingmoeny.exception;

public class SelfCropsMoneyException extends RuntimeException {
    public SelfCropsMoneyException() {
        super("자기가 뿌린돈은 자기가 받을 수 없습니다.");
    }
}
