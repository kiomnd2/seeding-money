package com.kakaopay.seedingmoeny.exception;

public class RequestFailedException extends RuntimeException {
    public RequestFailedException() {
        super("요청에 실패하였습니다. 다시 시도해 주시길 바랍니다.");
    }
}
