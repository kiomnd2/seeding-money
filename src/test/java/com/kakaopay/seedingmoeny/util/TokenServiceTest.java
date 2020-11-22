package com.kakaopay.seedingmoeny.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class TokenServiceTest {


    @DisplayName("토큰은 3자리 문자열로 구성되며 예측이 불가능 해야합니다")
    @Test
    void createToken() {
        String token = TokenGenerator.createToken();
        Assertions.assertThat(token.length()).isEqualTo(3);
    }
}
