package com.kakaopay.seedingmoeny.service;

import com.kakaopay.seedingmoeny.controller.SeedingRequest;
import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import com.kakaopay.seedingmoeny.exception.DuplicateCropsException;
import com.kakaopay.seedingmoeny.exception.NothingMoneyException;
import com.kakaopay.seedingmoeny.repository.CropsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kakaopay.seedingmoeny.util.MoneyDivideUtil.divide;

@RequiredArgsConstructor
@Service
public class CropsService {

    private final CropsRepository cropsRepository;

    /**
     * 아직 할당되지 않은 금액에 대한 수령처리
     * @param seeding 뿌리기 정보
     * @param requestUserId 요청자 아이디
     * @return 금액 할당 정보
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Crops harvesting(Seeding seeding, long requestUserId) {

        if (cropsRepository.existsBySeedingAndReceiveUserIdAndReceived(seeding, requestUserId, true)) {
            throw new DuplicateCropsException();
        }

        List<Crops> cropsList = cropsRepository.findAllBySeedingAndReceived(seeding, false);
        // 현재 남은 잔액이 없다면
        Crops crops = cropsList.stream().findFirst().orElseThrow(NothingMoneyException::new);

        crops.assign(requestUserId);

        return cropsRepository.save(crops);
    }

    /**
     * 금액을 나누어 저장합니다
     * @param request 요청값 { amount, receiverNumber }
     * @param seeding 뿌리기 정보
     */
    @Transactional
    public void divideCrops(SeedingRequest request, Seeding seeding) {
        // 금액을 나누어 저장
        divide(request).forEach(i -> {
            Crops crops = Crops.builder()
                    .seeding(seeding)
                    .received(false)
                    .receiveAmount(i)
                    .harvestAt(LocalDateTime.now())
                    .build();
            cropsRepository.save(crops);
        });
    }

}
