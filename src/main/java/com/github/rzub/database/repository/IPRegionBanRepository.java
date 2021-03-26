package com.github.rzub.database.repository;

import com.github.rzub.database.entity.IPRegionBanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface IPRegionBanRepository extends JpaRepository<IPRegionBanEntity, Integer> {
    void deleteAllByExpirationBefore(Date date);
    boolean existsByBlockHash(int hashCode);
}
