package com.kakaopay.seedingmoeny.repository;

import com.kakaopay.seedingmoeny.domain.SeedingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SeedingSession, String> {
}
