package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.domain.Token;
import com.kakaopay.seedingmoeny.domain.enums.TokenStatus;
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


    /*
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
