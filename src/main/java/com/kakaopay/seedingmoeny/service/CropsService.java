package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.dto.CropsDto;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CropsService {

    private final CropsRepository cropsRepository;

    private final SeedingSessionService seedingSessionService;

    public CropsDto harvesting(String roomId, long userId, String token) {


    }

}
