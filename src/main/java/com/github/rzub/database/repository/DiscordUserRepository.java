package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiscordUserRepository extends MongoRepository<DiscordUserEntity, String> {
    DiscordUserEntity findByUserId(String userId);
    DiscordUserEntity findByToken(String token);
}
