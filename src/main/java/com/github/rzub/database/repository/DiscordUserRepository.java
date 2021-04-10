package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscordUserRepository extends JpaRepository<DiscordUserEntity, Long> {
    DiscordUserEntity findByUserId(String userId);
    DiscordUserEntity findByToken(String token);
}
