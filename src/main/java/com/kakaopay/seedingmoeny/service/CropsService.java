package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.dto.CropsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CropsService {

    private final TokenService tokenService;


    @Transactional(readOnly = true)
    public CropsDto harvestMoney(String roomId, long userId, String token) {

        Token t = tokenService.getToken(token);


    }

}
