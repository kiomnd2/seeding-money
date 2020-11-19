package com.kakaopay.seedingmoeny.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.seedingmoeny.code.Codes;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.repository.TokenRepository;
import com.kakaopay.seedingmoeny.service.SeedingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
        cropsRepository.deleteAll();
        seedingRepository.deleteAll();
        tokenRepository.deleteAll();
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
    void request_seeding_return_token_success() throws Exception {
        long userID = 123;
        String roomID = "1234";

        SeedingRequest seedingRequest = new SeedingRequest(BigDecimal.valueOf(1000), 3);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .header("X-ROOM-ID", roomID)
                .content(mapper.writeValueAsBytes(seedingRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(Codes.S0000.code))
                .andExpect(jsonPath("message").value(Codes.S0000.desc))
                .andExpect(jsonPath("body.token").exists())
                .andExpect(jsonPath("body.issuedAt").exists());
    }


    @Test
    void request_seeding_return_token_fail() throws Exception {
        long userID = 123;
        String roomID = "1234";

        // 인원수가 돈보다 많다
        SeedingRequest seedingRequest = new SeedingRequest(BigDecimal.valueOf(2), 3);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .header("X-ROOM-ID", roomID)
                .content(mapper.writeValueAsBytes(seedingRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(Codes.E2000.code))
                .andExpect(jsonPath("message").value(Codes.E2000.desc))
                .andExpect(jsonPath("body").value(new InvalidAccessException().getMessage()));
    }


}
