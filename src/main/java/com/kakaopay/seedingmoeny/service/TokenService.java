package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.domain.enums.TokenStatus;
import com.kakaopay.seedingmoeny.exception.ExpiredSearchDateException;
import com.kakaopay.seedingmoeny.exception.InvalidTokenException;
import com.kakaopay.seedingmoeny.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public Token createToken() {
        String randomWord = generateRandomWord();

        if (tokenRepository.existsById(randomWord)) {
            randomWord = this.generateRandomWord(); // 이미 있으면 한번 더
        }
        Token token = Token.builder().value(randomWord).status(TokenStatus.CREATED).build();
        return tokenRepository.save(token);
    }

    public Token getToken(String token) {
        return getToken(token, true);
    }

    /**
     * 토큰을 조회하여 가져옵니다
     *
     * @param token     요청 토큰 정보
     * @param canSearch 조회를 위함인지 체크
     * @return 체크 후 반환된 토큰정보
     */
    @Transactional(readOnly = true)
    public Token getToken(String token, boolean canSearch) {
        // 실제로 해당 토큰이 존재하는지 ..
        Token t = tokenRepository.findById(token).orElseThrow(InvalidTokenException::new);

        // 토큰 유효성 검사
        if (t.isExpired() && !canSearch) { // 조회시엔 기한 초과인지 검사 안함
            throw new InvalidTokenException();
        }

        return t;
    }


    /**
     * 토큰 값을로 사용할 단어를 생성합니다.
     *
     * @return 숫자 문자를 조합한 랜덤한 3자리 문자
     */
    private String generateRandomWord() {
        Random rnd = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (rnd.nextBoolean()) {
                buf.append((char) ((rnd.nextInt(26)) + 97));
            } else {
                buf.append((rnd.nextInt(10)));
            }
        }
        return buf.toString();
    }

}
