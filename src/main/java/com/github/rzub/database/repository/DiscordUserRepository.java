package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface DiscordUserRepository extends JpaRepository<DiscordUserEntity, Long> {
    @Transactional
    DiscordUserEntity findByUserId(String userId);
    DiscordUserEntity findByToken(String token);
}
