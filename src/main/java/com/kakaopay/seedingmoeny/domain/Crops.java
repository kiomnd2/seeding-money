package com.kakaopay.seedingmoeny.domain;

import com.kakaopay.seedingmoeny.dto.CropsDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "CROPS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Crops {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seeding_id")
    private Seeding seeding;

    /**
     * 뿌린 금액을 받은 사용자 아이디
     */
    @Column(name = "receiver_id")
    private long receiveUserId;

    /**
     * 뿌린 금액을 받은 양
     */
    @Column(name = "receive_amount")
    private BigDecimal receiveAmount;

    /**
     * 뿌린 금앨을 받았는가
     */
    @Column(name = "is_received")
    private boolean received;

    /**
     * 돈을 받은 시간
     */
    @Column(name = "harvest_at")
    private LocalDateTime harvestAt;

    /**
     * 동시적 수정을 방지하기 위한 낙관적 업데이트
     */
    @Version
    private int version;

    @Builder
    public Crops(Seeding seeding, long receivedUserId, BigDecimal receiveAmount, boolean received, LocalDateTime harvestAt) {
        this.seeding = seeding;
        this.receiveUserId = receivedUserId;
        this.receiveAmount = receiveAmount;
        this.received = received;
        this.harvestAt = harvestAt;
    }


    /**
     * 사용자에게 뿌려진 돈을 할당합니다
     * @param userId 사용자 아이디
     */
    public void assign(long userId) {
        this.receiveUserId = userId;
        this.harvestAt = LocalDateTime.now();
        this.received = true;
    }

    public CropsDto getCropsDto() {
        return CropsDto.builder()
                .userId(receiveUserId)
                .harvestAt(this.getHarvestAt())
                .receiveAmount(this.getReceiveAmount())
                .build();
    }

}
