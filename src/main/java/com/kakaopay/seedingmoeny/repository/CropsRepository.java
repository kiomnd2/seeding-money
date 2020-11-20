package com.kakaopay.seedingmoeny.repository;

import com.kakaopay.seedingmoeny.domain.Crops;
import com.kakaopay.seedingmoeny.domain.Seeding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropsRepository extends JpaRepository<Crops, Long> {
    List<Crops> findAllBySeeding(Seeding seeding);

    boolean existsBySeedingAndReceiveUserIdAndReceived(Seeding seeding, long userId, boolean received);

    List<Crops> findAllBySeedingAndReceived(Seeding seeding, boolean received);

}
