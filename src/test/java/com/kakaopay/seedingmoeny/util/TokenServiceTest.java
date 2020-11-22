package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.util.TokenGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    private TokenGenerator tokenGenerator;

    @Description("토큰은 3자리 문자열로 구성되며 예측이 불가능 해야합니다")
    @Test
    void createToken() {
        String token = tokenGenerator.createToken();
        Assertions.assertThat(token.length()).isEqualTo(3);
    }
}
