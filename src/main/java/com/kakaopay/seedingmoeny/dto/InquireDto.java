package com.kakaopay.seedingmoeny.dto;

import com.kakaopay.seedingmoeny.domain.Crops;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@RequiredArgsConstructor
public class InquireDto {

    /**
     * 조회한 사용자
     */
    private final long userId;

    /**
     * 조회한 ROOM ID
     */
    private final String roomId;

    /**
     * 뿌려진 금액의 총량
     */
    private final BigDecimal totalAmount;

    /**
     * 이미 할당된 금액의 총량
     */
    private final BigDecimal usingAmount;

    /**
     * 금액을 뿌린 일시
     */
    private final LocalDateTime seedingAt;

    /**
     * 이미 수령 완료된 금액의 정보
     */
    private final List<CropsDto> cropsList;
}
