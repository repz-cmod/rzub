package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.IPRegionBanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface IPRegionBanRepository extends JpaRepository<IPRegionBanEntity, Integer> {
    void deleteAllByExpirationBefore(Date date);
    boolean existsByBlockHash(int hashCode);
}
