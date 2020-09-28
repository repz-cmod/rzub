package cmod.repz.application.database.repository;

import cmod.repz.application.config.ColumnName;
import cmod.repz.application.database.entity.PlayerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PlayerTrackerRepository extends JpaRepository<PlayerTrackEntity, Long> {
    void deleteAllByClientIdAndInIsNull(String clientId);
    void deleteAllByClientIdAndOutIsNull(String clientId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update #{#entityName} pt set pt.out = :out where pt."+ ColumnName.CLIENT_ID +" = :clientId AND pt."+ColumnName.TRACKER_ID+" = :trackerId AND pt."+ColumnName.SERVER_ID+" = :serverId")
    void updateOut(String clientId, String trackerId, String serverId, Date out);
}
