package com.kakaopay.seedingmoeny.domain;


import com.kakaopay.seedingmoeny.domain.enums.SeedingStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "seeding")
    private List<Crops> crops;

    @Builder
    public Seeding(String token, long userId, SeedingSession seedingSession, BigDecimal amount, SeedingStatus status, LocalDateTime seedingAt) {
        this.token = token;
        this.userId = userId;
        this.seedingSession = seedingSession;
        this.amount = amount;
        this.status = status;
        this.seedingAt = seedingAt;
    }


    public boolean isOwner(long userId) {
        return this.userId == userId;
    }

    /**
     * 토큰의 기한이 만료 되었는지 체크합니다
     *
     * @return 만료 여부
     */
    public boolean isExpired() {
        return status.equals(SeedingStatus.EXPIRED) || this.seedingAt.isBefore(LocalDateTime.now().minusMinutes(10));
    }

    /**
     * 조회기간이 만료 되었는지 여부를 체크합니다.
     *
     * @return 만룟 여부
     */
    public boolean canSearch() {
        return seedingAt.isAfter(LocalDateTime.now().minusDays(7));
    }


    /**
     * 뿌린 금액을 가져간 총량을 계산합니다.
     *
     * @return 계산된 값
     */
    public BigDecimal getUsingAmount() {
        return this.getHarvestedList().stream()
                .map(Crops::getReceiveAmount)
                .reduce(BigDecimal::add).map(v -> v.abs().setScale(2, RoundingMode.CEILING))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 수령된 금액의 정보 리스트를 가져옵니다
     *
     * @return 뿌린 정보에 대한 리스트
     */
    public List<Crops> getHarvestedList() {
        return this.crops.stream()
                .filter(Crops::isReceived)
                .collect(Collectors.toList());
    }


}
