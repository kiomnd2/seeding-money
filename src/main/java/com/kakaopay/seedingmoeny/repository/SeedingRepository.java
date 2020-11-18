package com.kakaopay.seedingmoeny.repository;

import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeedingRepository extends JpaRepository<Seeding, Long> {
    Optional<Seeding> findByTokenAndRoomId(Token token, String roomId);

    Optional<Seeding> findByTokenAndUserIdAndRoomId(Token token, long userId, String roomId);
}
