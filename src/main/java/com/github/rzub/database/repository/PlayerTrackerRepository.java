package com.github.rzub.database.repository;

import com.github.rzub.database.entity.PlayerTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PlayerTrackerRepository extends JpaRepository<PlayerTrackEntity, Long> {
    void deleteAllByClientIdAndJoinDateIsNull(Integer clientId);
    void deleteAllByClientIdAndLeaveDateIsNull(Integer clientId);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update #{#entityName} pt set pt.leaveDate = :leaveDate, pt.spentTime = :spentTime where pt.clientId = :clientId AND pt.trackerId = :trackerId AND pt.serverId = :serverId AND pt.spentTime IS NULL AND pt.leaveDate IS NULL")
    void updateLeftDate(Integer clientId, Long trackerId, Long serverId, Date leaveDate, int spentTime);
    PlayerTrackEntity findTop1ByClientIdAndTrackerIdAndServerIdOrderByIdDesc(Integer clientId, Long trackerId, Long serverId);
}
