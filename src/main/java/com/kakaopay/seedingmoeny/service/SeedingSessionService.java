package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.SeedingSession;
import com.kakaopay.seedingmoeny.exception.InvalidAccessException;
import com.kakaopay.seedingmoeny.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class SeedingSessionService {

    private final SessionRepository sessionRepository;

    /**
     * 최초 세션을 생성합니다. 이미 존재하는 세션이 있다면 존재하는 세션을 반환합니다.
     * @param roomId 방의 고유 아이디
     * @return 뿌리기 세션
     */
    @Transactional
    public SeedingSession createSeedingSession(String roomId) {
        return sessionRepository.findById(roomId).orElse(
                sessionRepository.save(SeedingSession.builder().roomId(roomId).createdAt(LocalDateTime.now()).build())
        );
    }


    public SeedingSession getSeedingSession(String roomId) {
        return sessionRepository.findById(roomId).orElseThrow(InvalidAccessException::new);
    }
}
