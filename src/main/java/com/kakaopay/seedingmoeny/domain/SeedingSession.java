package com.kakaopay.seedingmoeny.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "SEEDING_SESSIONS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SeedingSession {

    /**
     * 세션 룸 아이디
     */
    @Id @Column(name = "room_id")
    private String roomId;

    /**
     * 세션 생성 일자
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    protected SeedingSession(String roomId, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.createdAt = createdAt;
    }
}
