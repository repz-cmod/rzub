package com.github.rzub.database.repository;

import com.github.rzub.database.entity.WhitelistEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WhitelistRepository extends MongoRepository<WhitelistEntity, String> {
    void deleteByClientId(Integer clientId);
    boolean existsByClientId(Integer clientId);
}
