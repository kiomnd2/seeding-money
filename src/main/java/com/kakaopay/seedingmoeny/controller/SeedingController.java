package com.kakaopay.seedingmoeny.controller;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.service.SeedingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class SeedingController {

    private final SeedingService seedingService;

    /**
     * 방에 돈을 뿌립니다.
     * @param roomId 방의 고유 아이디
     * @param userId 사용자 아이디
     * @param request 요청값 { amount, receiverNumber }
     * @return
     */
    @PostMapping(value = "/api/seeding", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<SeedingDto>> seeding(@RequestHeader("X-ROOM-ID") String roomId,
                                                   @RequestHeader("X-USER-ID") long userId,
                                                   @RequestBody @Valid SeedingRequest request) {
        // 뿌리는 금액이 사람수보다 적을 순 없다..
        if (request.getAmount().intValue() < request.getReceiverNumber()) {
            throw new InvalidAccessException();
        }

        SeedingDto seedingDto = seedingService.seeding(roomId, userId, request);

        return ResponseEntity.ok().body(SeedingResponse.success(seedingDto));
    }

    /**
     *
     * @param roomId
     * @param userId
     * @param token
     * @return
     */
    @GetMapping(value = "/api/harvesting/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse> harvesting(@RequestHeader("X-ROOM-ID") String roomId,
                                                        @RequestHeader("X-USER-ID") long userId,
                                                        @PathVariable("token") String token) {


        return ResponseEntity.ok().build();
    }

}
