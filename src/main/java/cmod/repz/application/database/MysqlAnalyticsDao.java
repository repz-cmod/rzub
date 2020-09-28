package cmod.repz.application.database;

import cmod.repz.application.database.entity.PlayerTrackEntity;
import cmod.repz.application.database.entity.ServerTrackEntity;
import cmod.repz.application.database.repository.PlayerTrackerRepository;
import cmod.repz.application.database.repository.ServerTrackerRepository;
import cmod.repz.application.model.ServerStatusModel;
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
        playerTrackerRepository.deleteAllByClientIdAndJoinDateIsNull(clientId);
        playerTrackerRepository.deleteAllByClientIdAndLeftDateIsNull(clientId);

        playerTrackerRepository.save(PlayerTrackEntity.builder()
                .serverId(serverId)
                .clientId(clientId)
                .trackerId(trackerId)
                .joinDate(new Date())
                .build());
    }

    @Override
    public void playerLeft(String serverId, String clientId, String trackerId) {
        playerTrackerRepository.updateLeftDate(clientId, trackerId, serverId, new Date());
    }

    @Override
    public void trackServer(ServerStatusModel.Server server) {
        serverTrackerRepository.save(ServerTrackEntity.builder()
                .date(new Date())
                .playerCount(server.getCurrentPlayers())
                .serverId(String.valueOf(server.getId()))
                .game(server.getGame())
                .mapName(server.getMap().getName())
                .name(server.getName())
                .port(server.getPort())
                .build());
    }
}
