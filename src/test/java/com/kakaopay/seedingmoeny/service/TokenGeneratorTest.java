package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.util.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TokenGeneratorTest {

    @Autowired
    TokenGenerator tokenGenerator;

    @Test
    void createToken() {

    }


}
