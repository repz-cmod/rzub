package com.github.rzub.database.repository;

import com.github.rzub.database.entity.IPRegionBanEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface IPRegionBanRepository extends MongoRepository<IPRegionBanEntity, String> {
    void deleteAllByExpirationBefore(Date date);
    boolean existsByBlockHash(int hashCode);
}
