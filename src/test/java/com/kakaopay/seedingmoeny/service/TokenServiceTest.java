package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.repository.TokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    void beforeEach() {
        tokenRepository.deleteAll();
    }

    @Test
    void createToken() {
        Token token = tokenService.createToken();

        assertNotNull(token.getValue());
        assertThat(token.getValue().length()).isEqualTo(3);
    }


}
