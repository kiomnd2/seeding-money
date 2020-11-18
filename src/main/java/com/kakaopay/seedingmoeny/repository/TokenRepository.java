package com.kakaopay.seedingmoeny.repository;

import com.kakaopay.seedingmoeny.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
