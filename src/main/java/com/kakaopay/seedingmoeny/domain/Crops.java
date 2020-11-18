package com.kakaopay.seedingmoeny.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
    @Column(name = "user_id")
    private long receiveUserId;

    /**
     * 뿌린 금액을 받은 양
     */
    @Column(name = "amount")
    private BigDecimal receiveAmount;

    /**
     * 뿌린 금앨을 받았는가
     */
    @Column(name = "is_received")
    private boolean received;

    /**
     * 돈을 받은 시간
     */
    @Column(name = "picked_at")
    private LocalDateTime pickedAt;

    /**
     * 동시적 수정을 방지하기 위한 낙관적 업데이트
     */
    @Version
    private int version;

    @Builder
    public Crops(Seeding seeding, long receivedUserId, BigDecimal receiveAmount, boolean received) {
        this.seeding = seeding;
        this.receiveUserId = receivedUserId;
        this.receiveAmount = receiveAmount;
        this.received = received;
    }

}
