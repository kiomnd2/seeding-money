package com.kakaopay.seedingmoeny.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "SEEDING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Seeding {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 토큰 값
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private Token token;

    /**
     * 돈을 뿌린 사용자
     */
    @Column(name = "user_id")
    private long userId;

    /**
     * 돈을 뿌린 방 번호
     */
    @Column(name = "room_id")
    private String roomId;

    /**
     * 뿌린 돈의 양
     */
    @Column(name = "amount")
    private BigDecimal amount;


    @Builder
    public Seeding(Token token, long userId, String roomId, BigDecimal amount) {
        this.token = token;
        this.userId = userId;
        this.roomId = roomId;
        this.amount = amount;
    }



}
