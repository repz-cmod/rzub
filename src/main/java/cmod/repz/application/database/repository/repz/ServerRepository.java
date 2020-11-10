package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.ServerEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface ServerRepository extends JpaRepository<ServerEntity, Long> {
    boolean existsByServerId(long id);
    @Transactional(rollbackOn = DataIntegrityViolationException.class)
    ServerEntity save(ServerEntity serverEntity);
}
