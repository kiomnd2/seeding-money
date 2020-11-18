package com.kakaopay.seedingmoeny.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MoneyDividerComponentTest {

    @Autowired
    MoneyDividerComponent moneyDividerComponent;

    @Test
    void divideTest() {
        int receiveNumber = 3;
        BigDecimal amount = BigDecimal.valueOf(1000);
        SeedingRequest seedingRequest = new SeedingRequest( amount, receiveNumber);
        List<BigDecimal> divide = moneyDividerComponent.divide(seedingRequest);


        BigDecimal bigDecimal = divide.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        assertThat(divide.size()).isEqualTo(receiveNumber);
        assertThat(bigDecimal).isEqualByComparingTo(amount);
    }
}
