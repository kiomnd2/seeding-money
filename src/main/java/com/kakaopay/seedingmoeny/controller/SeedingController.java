package com.kakaopay.seedingmoeny.controller;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.dto.CropsDto;
import com.kakaopay.seedingmoeny.dto.InquireDto;
import com.kakaopay.seedingmoeny.dto.SeedingDto;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.service.CropsService;
import com.kakaopay.seedingmoeny.service.SeedingService;
import com.kakaopay.seedingmoeny.service.SeedingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SeedingController {

    private final SeedingService seedingService;

    private final CropsService cropsService;

    private final SeedingSessionService seedingSessionService;


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

        // 최초 뿌리기 세션정보를 가져옴
        SeedingSession seedingSession = seedingSessionService.createSeedingSession(roomId);

        // 뿌리기 정보 생성
        Seeding seeding = seedingService.seeding(userId, seedingSession, request);

        // 수령할 금액 분배
        cropsService.divideCrops(request, seeding);

        // 사용자에게 돌려줄 DTO 생성
        SeedingDto seedingDto = SeedingDto.builder().token(seeding.getToken()).issuedAt(LocalDateTime.now()).build();

        return ResponseEntity.ok().body(SeedingResponse.success(seedingDto));
    }

    /**
     * 뿌려진 돈을 수확합니다
     * @param roomId
     * @param userId
     * @param token
     * @return
     */
    @PutMapping(value = "/api/harvest/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<CropsDto>> harvesting(@RequestHeader("X-ROOM-ID") String roomId,
                                                                @RequestHeader("X-USER-ID") long userId,
                                                                @PathVariable("token") String token) {
        // 사용자, 세션, 토큰에 대한 정합성검사
        SeedingSession seedingSession = seedingSessionService.getSeedingSession(roomId);

        Seeding seeding = seedingService.checkSeeding(userId, seedingSession, token);

        Crops harvestedCrops = cropsService.harvesting(seeding, userId);

        CropsDto cropsDto = CropsDto.builder()
                .userId(userId)
                .harvestAt(harvestedCrops.getHarvestAt())
                .receiveAmount(harvestedCrops.getReceiveAmount())
                .build();

        return ResponseEntity.ok().body(SeedingResponse.success(cropsDto));
    }

    @GetMapping(value = "/api/inquire/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeedingResponse<InquireDto>> inquire(@RequestHeader("X-ROOM-ID") String roomId,
                                                               @RequestHeader("X-USER-ID") long userId,
                                                               @PathVariable("token") String token) {

        // 사용자, 세션, 토큰에 대한 정합성검사
        SeedingSession seedingSession = seedingSessionService.getSeedingSession(roomId);

        // 정합성 검사
        Seeding seeding = seedingService.checkInquire(userId, seedingSession, token);

        // 현재 조회 리스트
        List<CropsDto> cropsDtos = seeding.getHarvestedList().stream().map(v -> CropsDto.builder()
                .userId(v.getReceiveUserId())
                .receiveAmount(v.getReceiveAmount())
                .harvestAt(v.getHarvestAt()).build()).collect(Collectors.toList());


        InquireDto inquireDto = InquireDto.builder()
                .userId(seeding.getUserId())
                .roomId(seedingSession.getRoomId())
                .seedingAt(seeding.getSeedingAt())
                .cropsList(cropsDtos)
                .totalAmount(seeding.getAmount())
                .usingAmount(seeding.getUsingAmount())
                .build();


        return ResponseEntity.ok().body(SeedingResponse.success(inquireDto));
    }


}
