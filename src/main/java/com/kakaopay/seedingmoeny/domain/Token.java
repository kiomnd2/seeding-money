package com.kakaopay.seedingmoeny.domain;

import com.kakaopay.seedingmoeny.domain.enums.TokenStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "TOKEN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Token {

    /**
     * 토큰 키값
     */
    @Id
    @Column(name = "token_id", length = 3, nullable = false)
    private String value;

    /**
     * 현재 토큰의 상태를 표시
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TokenStatus status = TokenStatus.CREATED;

    /**
     * 토큰 발행 일
     */
    @CreatedDate
    @Column(name = "issued_at", nullable = false)
    private final LocalDateTime issuedAt = LocalDateTime.now();

    @Builder
    protected Token(String value, TokenStatus status) {
        this.value = value;
        this.status = status;
    }
}
