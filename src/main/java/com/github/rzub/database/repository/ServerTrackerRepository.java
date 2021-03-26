package com.github.rzub.database.repository;

import com.github.rzub.database.entity.ServerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTrackerRepository extends JpaRepository<ServerTrackEntity, Long> {
}
