package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IW4MAdminUserRepository extends MongoRepository<IW4MAdminUserEntity, String> {
    Optional<IW4MAdminUserEntity> findByDiscordUserAndGame(DiscordUserEntity discordUser, String game);
    List<IW4MAdminUserEntity> findTop1ByDiscordUserAndClientId(DiscordUserEntity discordUser, long clientId);
    List<IW4MAdminUserEntity> findAllByDiscordUser(DiscordUserEntity discordUser);
}
