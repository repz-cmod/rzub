package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.ServerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerTrackerRepository extends JpaRepository<ServerTrackEntity, Long> {
}
