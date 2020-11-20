package com.kakaopay.seedingmoeny.repository;

import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.domain.SeedingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeedingRepository extends JpaRepository<Seeding, Long> {
    Optional<Seeding> findByTokenAndSeedingSession(String token, SeedingSession seedingSession);
}
