package com.kakaopay.seedingmoeny.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.seedingmoeny.code.Codes;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import com.kakaopay.seedingmoeny.exception.ExpiredSearchDateException;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.exception.SelfCropsMoneyException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import com.kakaopay.seedingmoeny.repository.SeedingRepository;
import com.kakaopay.seedingmoeny.service.CropsService;
import com.kakaopay.seedingmoeny.service.SeedingRequest;
import com.kakaopay.seedingmoeny.service.SeedingService;
import com.kakaopay.seedingmoeny.service.SeedingSessionService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.kakaopay.seedingmoeny.util.TokenGenerator.createToken;
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


    @DisplayName("뿌릴금액, 인원을 요청값으로 받습니다 (헤더없음 실패)")
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

    @DisplayName("뿌릴금액, 인원을 요청값으로 받습니다 (성공)")
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


    @DisplayName("뿌릴금액, 인원을 요청값으로 받습니다 ( 인원이 돈보다 많아 실패 )")
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


    @DisplayName("아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당합니다.")
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

        long anotherUser = 444;

        mockMvc.perform(put("/api/harvest/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", anotherUser)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(Codes.S0000.code))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body.userId").value(anotherUser))
                .andExpect(jsonPath("body.receiveAmount").value(Matchers.greaterThan(0.0)));
    }

    @DisplayName("자신이 뿌리기한 건은 자신이 받을 수 없습니다 ( 자신이 받아 실패)")
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
                .andExpect(jsonPath("code").value(Codes.E2000.code))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value(new SelfCropsMoneyException().getMessage()));
    }


    @DisplayName("token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다 ")
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
        long anotherUser = 222;
        Crops crops = cropsService.harvesting(seeding, anotherUser);

        // 모든 정보 조회
        mockMvc.perform(get("/api/inquire/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(Codes.S0000.code))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body.userId").value(userId))
                .andExpect(jsonPath("body.roomId").value(seedingSession.getRoomId()))
                .andExpect(jsonPath("body.totalAmount").value(Matchers.comparesEqualTo(amount.doubleValue())))
                .andExpect(jsonPath("body.usingAmount").value(crops.getReceiveAmount().doubleValue()))
                .andExpect(jsonPath("body.cropsList" , hasSize(1)));
    }

    @DisplayName("뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다")
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

        long anotherUser = 333;
        // 모든 정보 조회
        mockMvc.perform(get("/api/inquire/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", anotherUser)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(Codes.E2000.code))
                .andExpect(jsonPath("body").value(new InvalidAccessException().getMessage()));
    }

    @DisplayName("뿌린 건의 조회는 7일 동안 할 수 있습니다")
    @Test
    void request_inquire_expire_fail() throws Exception {

        String roomId ="123";
        BigDecimal amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.CEILING);
        long userId = 111;
        int receiverNumber = 2;

        SeedingRequest seedingRequest = new SeedingRequest(amount ,receiverNumber);

        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);
        // 토큰 생성
        String token = createToken();

        // 10일 이후에 조회
        Seeding seeding = Seeding.builder()
                .token(token)
                .userId(userId)
                .amount(seedingRequest.getAmount())
                .seedingSession(seedingSession)
                .seedingAt(LocalDateTime.now().minusDays(10))
                .status(SeedingStatus.CREATED)
                .build();
        seedingRepository.save(seeding);

        cropsService.divideCrops(seedingRequest, seeding);

        // 모든 정보 조회
        mockMvc.perform(get("/api/inquire/" + seeding.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userId)
                .header("X-ROOM-ID", roomId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(Codes.E2000.code))
                .andExpect(jsonPath("body").value(new ExpiredSearchDateException().getMessage()));
    }




}
