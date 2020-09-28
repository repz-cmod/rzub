package cmod.repz.application.database;

import cmod.repz.application.database.entity.PlayerTrackEntity;
import cmod.repz.application.database.entity.ServerTrackEntity;
import cmod.repz.application.database.repository.PlayerTrackerRepository;
import cmod.repz.application.database.repository.ServerTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MysqlAnalyticsDao implements AnalyticsDao {
    private final PlayerTrackerRepository playerTrackerRepository;
    private final ServerTrackerRepository serverTrackerRepository;

    @Autowired
    public MysqlAnalyticsDao(PlayerTrackerRepository playerTrackerRepository, ServerTrackerRepository serverTrackerRepository) {
        this.playerTrackerRepository = playerTrackerRepository;
        this.serverTrackerRepository = serverTrackerRepository;
    }

    @Override
    public void playerJoined(String serverId, String clientId, String trackerId) {
        playerTrackerRepository.deleteAllByClientIdAndInIsNull(clientId);
        playerTrackerRepository.deleteAllByClientIdAndOutIsNull(clientId);

        playerTrackerRepository.save(PlayerTrackEntity.builder()
                .serverId(serverId)
                .clientId(clientId)
                .trackerId(trackerId)
                .in(new Date())
                .build());
    }

    @Override
    public void playerLeft(String serverId, String clientId, String trackerId) {
        playerTrackerRepository.updateOut(clientId, trackerId, serverId, new Date());
    }

    @Override
    public void trackServer(String serverId, int playerCount) {
        serverTrackerRepository.save(ServerTrackEntity.builder()
                .date(new Date())
                .playerCount(playerCount)
                .serverId(serverId)
                .build());
    }
}
