package cmod.repz.application.database.repository.xlr.mw2;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XlrMw2StatsRepository extends JpaRepository<XlrPlayerStatEntity, Integer> {
    XlrPlayerStatEntity findByClientId(int clientId);
}
