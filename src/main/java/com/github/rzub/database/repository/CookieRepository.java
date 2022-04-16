package com.github.rzub.database.repository;

import com.github.rzub.database.entity.CookieEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CookieRepository extends MongoRepository<CookieEntity, String> {
    Optional<CookieEntity> findTop1ByIw4MAdminUserIsNullOrderByIdDesc();
    Optional<CookieEntity> findTop1ByIw4MAdminUserOrderByIdDesc(IW4MAdminUserEntity iw4MAdminUserEntity);
}
