package com.kakaopay.seedingmoeny.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.repository.TokenRepository;
import com.kakaopay.seedingmoeny.service.SeedingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SeedingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;


    @BeforeEach
    void beforeEach() {
        tokenRepository.deleteAll();
        cropsRepository.deleteAll();
        seedingRepository.deleteAll();
    }


    @Test
    void request_seeding_success() throws Exception {
        long userID = 123;
        String roomID = "1234";

        SeedingRequest seedingRequest = new SeedingRequest(BigDecimal.valueOf(1000), 3);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .header("X-ROOM-ID", roomID)
                .content(mapper.writeValueAsBytes(seedingRequest))
        )
        .andDo(print())
        .andExpect(status().isOk());
    }

    @Test
    void request_not_header_fail() throws Exception {
        long userID = 123;
        SeedingRequest seedingRequest = new SeedingRequest(BigDecimal.valueOf(1000), 123);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .content(mapper.writeValueAsBytes(seedingRequest))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void request_seeding_return_token() throws Exception {
        long userID = 123;
        String roomID = "1234";

        SeedingRequest seedingRequest = new SeedingRequest(BigDecimal.valueOf(1000), 3);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .header("X-ROOM-ID", roomID)
                .content(mapper.writeValueAsBytes(seedingRequest)))
                .andExpect(status().isOk());
    }



}
