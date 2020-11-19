package com.kakaopay.seedingmoeny.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class MoneyDivideUtilTest {

    @Autowired
    MoneyDivideUtil divider;

    @Test
    void divideTest() {
        int receiveNumber = 3;
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest( amount, receiveNumber);
        List<BigDecimal> divide = divider.divide(seedingRequest);


        BigDecimal sum = divide.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        assertThat(divide.size()).isEqualTo(receiveNumber);
        assertThat(sum).isEqualTo(amount);
    }
}
