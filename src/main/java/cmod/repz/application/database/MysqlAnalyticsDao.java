package cmod.repz.application.database;

import cmod.repz.application.database.entity.PlayerTrackEntity;
import cmod.repz.application.database.entity.ServerEntity;
import cmod.repz.application.database.entity.ServerTrackEntity;
import cmod.repz.application.database.repository.PlayerTrackerRepository;
import cmod.repz.application.database.repository.ServerRepository;
import cmod.repz.application.database.repository.ServerTrackerRepository;
import cmod.repz.application.model.ServerStatusModel;
import cmod.repz.application.util.GameUtil;
import cmod.repz.application.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
public class MysqlAnalyticsDao implements AnalyticsDao {
    private final PlayerTrackerRepository playerTrackerRepository;
    private final ServerTrackerRepository serverTrackerRepository;
    private final ServerRepository serverRepository;
    private final Set<Long> persistedServerIds;

    @Autowired
    public MysqlAnalyticsDao(PlayerTrackerRepository playerTrackerRepository, ServerTrackerRepository serverTrackerRepository, ServerRepository serverRepository, Set<Long> persistedServerIds) {
        this.playerTrackerRepository = playerTrackerRepository;
        this.serverTrackerRepository = serverTrackerRepository;
        this.serverRepository = serverRepository;
        this.persistedServerIds = persistedServerIds;
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
                .mapName(server.getMap().getName())
                .percentage(MathUtil.percent(server.getCurrentPlayers(), server.getMaxPlayers()))
                .build());
        addServer(server);
    }

    private synchronized void addServer(ServerStatusModel.Server server){
        if(persistedServerIds.contains(server.getId()))
            return;
        try{
            serverRepository.save(ServerEntity.builder()
                    .game(server.getGame())
                    .name(GameUtil.cleanColors(server.getName()))
                    .port(server.getPort())
                    .serverId(String.valueOf(server.getId()))
                    .build());

        }catch (DuplicateKeyException ignored){}
        persistedServerIds.add(server.getId());
    }
}
