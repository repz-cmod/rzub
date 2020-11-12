package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.GungamePlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface GungamePlayerRepository extends JpaRepository<GungamePlayerEntity, Integer> {
    GungamePlayerEntity findByGuid(String guid);
    boolean existsByGuid(String guid);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update #{#entityName} gp set gp.wins = gp.wins + 1 where gp.guid = :guid")
    void increaseWins(String guid);
    int countAllByWinsGreaterThan(int wins);
}
