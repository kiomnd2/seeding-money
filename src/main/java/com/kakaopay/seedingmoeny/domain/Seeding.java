package com.kakaopay.seedingmoeny.domain;


import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "SEEDINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Seeding {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 토큰 값
     */
    @Column(name = "token_id")
    private String token;

    /**
     * 주최자
     */
    @Column(name = "user_id")
    private long userId;

    /**
     * 세션 고유값
     */
    @JoinColumn(name = "seeding_session")
    @ManyToOne(fetch = FetchType.LAZY)
    private SeedingSession seedingSession;

    /**
     * 뿌린 돈의 양
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * 현재 뿌리기 상태
     */
    @Column(name = "seeding_status")
    private SeedingStatus status;

    /**
     * 뿌린 일자
     */
    @Column(name = "seeding_at")
    private LocalDateTime seedingAt;


    @Builder
    public Seeding(String token, long userId, SeedingSession seedingSession, BigDecimal amount, SeedingStatus status, LocalDateTime seedingAt) {
        this.token = token;
        this.userId = userId;
        this.seedingSession = seedingSession;
        this.amount = amount;
        this.status = status;
        this.seedingAt = seedingAt;
    }



}
