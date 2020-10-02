package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.PlayerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PlayerTrackerRepository extends JpaRepository<PlayerTrackEntity, Long> {
    void deleteAllByClientIdAndJoinDateIsNull(String clientId);
    void deleteAllByClientIdAndLeaveDateIsNull(String clientId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update #{#entityName} pt set pt.leaveDate = :leaveDate where pt.clientId = :clientId AND pt.trackerId = :trackerId AND pt.serverId = :serverId")
    void updateLeftDate(String clientId, String trackerId, String serverId, Date leaveDate);
}
