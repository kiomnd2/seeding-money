package com.kakaopay.seedingmoeny.util;

import com.kakaopay.seedingmoeny.service.SeedingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.kakaopay.seedingmoeny.util.MoneyDivideUtil.divide;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
class MoneyDivideUtilTest {

    @DisplayName("뿌린돈이 적절하게 분배 되었는지 테스트합니다.")
    @Test
    void divideTest() {
        int receiveNumber = 3;
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        SeedingRequest seedingRequest = new SeedingRequest( amount, receiveNumber);
        List<BigDecimal> divide = divide(seedingRequest);


        BigDecimal sum = divide.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        assertThat(divide.size()).isEqualTo(receiveNumber);
        assertThat(sum).isEqualTo(amount);
    }
}
