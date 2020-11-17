package com.kakaopay.seedingmoeny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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


    @Test
    public void request_seeding_success() throws Exception {
        String userID = "123";
        String roomID = "1234";
        SeedingRequest seedingRequest = new SeedingRequest(1000, 123);


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
    public void request_not_header_fail() throws Exception {
        String userID = "123";
        SeedingRequest seedingRequest = new SeedingRequest(1000, 123);

        mockMvc.perform(post("/api/seeding")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USER-ID", userID)
                .content(mapper.writeValueAsBytes(seedingRequest))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
