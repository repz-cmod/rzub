package com.github.rzub.database.repository;

import com.github.rzub.database.entity.CookieEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CookieRepository extends JpaRepository<CookieEntity, Integer> {
    Optional<CookieEntity> findTop1ByIw4MAdminUserIsNullOrderByIdDesc();
    Optional<CookieEntity> findTop1ByIw4MAdminUserOrderByIdDesc(IW4MAdminUserEntity iw4MAdminUserEntity);
}
