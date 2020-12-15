package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.WhitelistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhitelistRepository extends JpaRepository<WhitelistEntity, Integer> {
    void deleteByClientId(Integer clientId);
    boolean existsByClientId(Integer clientId);
}
