package com.github.rzub.database.repository;

import com.github.rzub.database.entity.WhitelistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhitelistRepository extends JpaRepository<WhitelistEntity, Integer> {
    void deleteByClientId(Integer clientId);
    boolean existsByClientId(Integer clientId);
}
