package com.kakaopay.seedingmoeny.controller;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.dto.CropsDto;
import com.kakaopay.seedingmoeny.dto.InquireDto;
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
     * @return 뿌려진 정보
     */
    @PostMapping(value = "/api/seeding", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<SeedingDto>> seeding(@RequestHeader("X-ROOM-ID") String roomId,
                                                   @RequestHeader("X-USER-ID") long userId,
                                                   @RequestBody @Valid SeedingRequest request) {
        // 뿌리는 금액이 사람수보다 적을 순 없다..
        if (request.getAmount().intValue() < request.getReceiverNumber()) {
            throw new InvalidAccessException();
        }

        // 뿌리기 정보 생성
        Seeding seeding = seedingService.seeding(userId, roomId, request);

        return ResponseEntity.ok().body(SeedingResponse.success(seeding.getSeedingDto()));
    }

    /**
     * 뿌려진 돈을 수확합니다
     * @param roomId 방의 고유 아이디
     * @param userId 사용자 아이디
     * @param token 토큰 값
     * @return 돈을 수확한 정보
     */
    @PutMapping(value = "/api/harvest/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<CropsDto>> harvesting(@RequestHeader("X-ROOM-ID") String roomId,
                                                                @RequestHeader("X-USER-ID") long userId,
                                                                @PathVariable("token") String token) {

        Crops crops = seedingService.harvest(userId, roomId, token);

        return ResponseEntity.ok().body(SeedingResponse.success(crops.getCropsDto()));
    }

    /**
     * 현재 뿌리기 정보에 대한 조회를 진행합니다.
     * @param roomId 방유 고유 아이디
     * @param userId 사용자 아아디
     * @param token 토큰 값
     * @return 조회 정보
     */
    @GetMapping(value = "/api/inquire/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<InquireDto>> inquire(@RequestHeader("X-ROOM-ID") String roomId,
                                                               @RequestHeader("X-USER-ID") long userId,
                                                               @PathVariable("token") String token) {

        // 정합성 검사
        Seeding seeding = seedingService.inquire(userId, roomId, token);

        return ResponseEntity.ok().body(SeedingResponse.success(seeding.getInquireDto()));
    }
}
