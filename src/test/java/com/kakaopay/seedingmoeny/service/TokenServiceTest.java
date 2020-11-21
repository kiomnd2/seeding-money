package com.kakaopay.seedingmoeny.util;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.service.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @Test
    void createToken() {
        Token token = tokenService.createToken();
        Assertions.assertThat(token.getValue().length()).isEqualTo(3);
    }
}
