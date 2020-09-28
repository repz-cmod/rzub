package cmod.repz.application.database.repository;

import cmod.repz.application.database.entity.PlayerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerTrackerRepository extends JpaRepository<PlayerTrackEntity, Long> {
}
