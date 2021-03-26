package com.github.rzub.database.repository;

import com.github.rzub.database.entity.DiscordUserConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscordUserConfigRepository extends JpaRepository<DiscordUserConfigEntity, Long> {

}
