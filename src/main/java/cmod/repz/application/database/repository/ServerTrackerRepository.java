package cmod.repz.application.database.repository;

import cmod.repz.application.database.entity.ServerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTrackerRepository extends JpaRepository<ServerTrackEntity, Long> {
}
