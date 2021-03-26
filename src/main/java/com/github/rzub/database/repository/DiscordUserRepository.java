package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscordUserRepository extends JpaRepository<DiscordUserEntity, Long> {
    DiscordUserEntity findByUserId(String userId);
    DiscordUserEntity findByToken(String token);
    List<DiscordUserEntity> findAllByB3MW2ClientIdIn(List<String> clientIds);
    List<DiscordUserEntity> findAllByB3BO2ClientIdIn(List<String> clientIds);
    DiscordUserEntity findByIw4adminBo2ClientId(String clientId);
    DiscordUserEntity findByIw4adminMw2ClientId(String clientId);
}
