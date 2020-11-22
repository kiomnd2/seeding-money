package com.kakaopay.seedingmoeny.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class TokenGenerator {


    public static String createToken() {
        return generateRandomWord();
    }

    /**
     * 토큰 값을로 사용할 단어를 생성합니다.
     *
     * @return 숫자 문자를 조합한 랜덤한 3자리 문자
     */
    private String generateRandomWord() {
        Random rnd = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (rnd.nextBoolean()) {
                buf.append((char) ((rnd.nextInt(26)) + 97));
            } else {
                buf.append((rnd.nextInt(10)));
            }
        }
        return buf.toString();
    }

}
