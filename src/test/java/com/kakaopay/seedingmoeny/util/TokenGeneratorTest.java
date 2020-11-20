package com.kakaopay.seedingmoeny.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenGeneratorTest {

    @Autowired
    TokenGenerator tokenGenerator;

    @Test
    void createToken() {
        String token = tokenGenerator.createToken();
        Assertions.assertThat(token.length()).isEqualTo(3);
    }
}
