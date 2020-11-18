package com.kakaopay.seedingmoeny.code;

public enum Codes {
    S0000("0000", "정상"),
    E2000("2000", "잘못된 요청");

    public final String code;

    public final String desc;

    Codes(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
