package com.kakaopay.seedingmoeny.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
public class SeedingRequest extends com.kakaopay.seedingmoeny.controller.SeedingRequest {

    public SeedingRequest( BigDecimal amount, int receiverNumber) {
        super(amount, receiverNumber);
    }
}
