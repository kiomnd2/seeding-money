package com.kakaopay.seedingmoeny.exception;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class CropsFailedException extends ObjectOptimisticLockingFailureException {
    public CropsFailedException(String msg, Throwable cause) {
        super("요청에 실패하였습니다 다시 시도해 주세요", cause);
    }
}
