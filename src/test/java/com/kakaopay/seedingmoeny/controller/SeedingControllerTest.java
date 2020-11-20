package com.kakaopay.seedingmoeny.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.seedingmoeny.code.Codes;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.service.CropsService;
import com.kakaopay.seedingmoeny.service.SeedingRequest;
import com.kakaopay.seedingmoeny.service.SeedingService;
import com.kakaopay.seedingmoeny.service.SeedingSessionService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    CropsRepository cropsRepository;

    @Autowired
    SeedingRepository seedingRepository;

    @Autowired
    SeedingSessionService seedingSessionService;

    @Autowired
    SeedingService seedingService;

    @Autowired
    CropsService cropsService;


    @BeforeEach
    void beforeEach() {
        cropsRepository.deleteAll();
        seedingRepository.deleteAll();
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


    @Test
    void request_crops_return_money_success() throws Exception {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        long user2 = 444;

        mockMvc.perform(put("/api/harvest/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", user2)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0000"))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body.userId").value(user2))
                .andExpect(jsonPath("body.receiveAmount").value(Matchers.greaterThan(0.0)));
    }

    @Test
    void request_crops_return_money_fail() throws Exception {
        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);

        // 같은 유저가 수령하려 했을 때 오류 발생해야함
        mockMvc.perform(put("/api/harvest/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("2000"))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").isString());
    }


    @Test
    void request_inquire_success() throws Exception {

        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);


        // 1 회 수확
        long user2 = 222;
        Crops crops = cropsService.harvesting(seeding, user2);

        // 모든 정보 조회
        mockMvc.perform(get("/api/inquire/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value("0000"))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body.userId").value(userId))
                .andExpect(jsonPath("body.roomId").value(seedingSession.getRoomId()))
                .andExpect(jsonPath("body.totalAmount").value(Matchers.comparesEqualTo(amount.doubleValue())))
                .andExpect(jsonPath("body.usingAmount").value(crops.getReceiveAmount()))
                .andExpect(jsonPath("body.cropsList" , hasSize(1)));
    }


    @Test
    void request_inquire_invalid_user_fail() throws Exception {

        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        Seeding seeding = seedingService.seeding(userId, seedingSession, seedingRequest);

        cropsService.divideCrops(seedingRequest, seeding);


        // 모든 정보 조회
        mockMvc.perform(get("/api/inquire/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", "333")
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value("2000"));
    }

}
