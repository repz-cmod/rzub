package com.github.rzub.database.repository;

import com.github.rzub.database.entity.TextConfigurationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextConfigurationRepository extends MongoRepository<TextConfigurationEntity, String> {
    Optional<TextConfigurationEntity> findByName(String name);
}
