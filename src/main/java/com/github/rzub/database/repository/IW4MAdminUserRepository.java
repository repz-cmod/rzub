package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IW4MAdminUserRepository extends JpaRepository<IW4MAdminUserEntity, Long> {
    Optional<IW4MAdminUserEntity> findByDiscordUserAndGame(DiscordUserEntity discordUser, String game);
}
