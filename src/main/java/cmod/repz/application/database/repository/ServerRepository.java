package cmod.repz.application.database.repository;

import cmod.repz.application.database.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<ServerEntity, Long> {
}
