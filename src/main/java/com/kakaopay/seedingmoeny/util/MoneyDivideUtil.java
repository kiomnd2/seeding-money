package com.kakaopay.seedingmoeny.util;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MoneyDivideUtil {

    /**
     * 금액을 분배하여 리스트로 리턴합니다.
     * @param request 계산을 위한 사람수, 돈의 양
     * @return 돈을 나눈 리스트
     */
    public List<BigDecimal> divide(SeedingRequest request) {
        ArrayList<BigDecimal> list = new ArrayList<>();
        Random random = new Random();

        BigDecimal amount = request.getAmount();
        int numberOfPeople = request.getReceiverNumber();

        BigDecimal temp;

        for (int i = 0 ; i < numberOfPeople ; i++ ) {
            if( i == numberOfPeople - 1 ) {
                list.add(amount);
                break;
            }
            temp = amount.multiply(BigDecimal.valueOf(random.nextDouble()*0.4).abs()).setScale(2, RoundingMode.CEILING);
            BigDecimal s = amount.subtract(temp , MathContext.DECIMAL32);
            list.add(temp);
            amount = s;
        }

        return list;
    }
}
