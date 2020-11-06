package cmod.repz.application.database;

import cmod.repz.application.database.entity.repz.PlayerTrackEntity;
import cmod.repz.application.database.entity.repz.ServerEntity;
import cmod.repz.application.database.entity.repz.ServerTrackEntity;
import cmod.repz.application.database.repository.repz.PlayerTrackerRepository;
import cmod.repz.application.database.repository.repz.ServerRepository;
import cmod.repz.application.database.repository.repz.ServerTrackerRepository;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.util.GameUtil;
import cmod.repz.application.util.MathUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class MysqlAnalyticsDao implements AnalyticsDao {
    private final PlayerTrackerRepository playerTrackerRepository;
    private final ServerTrackerRepository serverTrackerRepository;
    private final ServerRepository serverRepository;
    private final Set<Long> persistedServerIds;

    @Autowired
    public MysqlAnalyticsDao(PlayerTrackerRepository playerTrackerRepository, ServerTrackerRepository serverTrackerRepository, ServerRepository serverRepository) {
        this.playerTrackerRepository = playerTrackerRepository;
        this.serverTrackerRepository = serverTrackerRepository;
        this.serverRepository = serverRepository;
        this.persistedServerIds = new HashSet<>();
    }

    @Override
    @Transactional
    public void playerJoined(Long serverId, Integer clientId, Long trackerId) {
        playerTrackerRepository.deleteAllByClientIdAndJoinDateIsNull(clientId);
        playerTrackerRepository.deleteAllByClientIdAndLeaveDateIsNull(clientId);

        playerTrackerRepository.save(PlayerTrackEntity.builder()
                .serverId(serverId)
                .clientId(clientId)
                .trackerId(trackerId)
                .joinDate(new Date())
                .build());
    }

    @Override
    @Transactional
    public void playerLeft(Long serverId, Integer clientId, Long trackerId) {
        PlayerTrackEntity playerTrackEntity = playerTrackerRepository.findByClientIdAndTrackerIdAndServerId(clientId, trackerId, serverId);
        if(playerTrackEntity != null){
            int spentTime = (int) ((new Date().getTime() - playerTrackEntity.getJoinDate().getTime()) / 1000);
            playerTrackerRepository.updateLeftDate(clientId, trackerId, serverId, new Date(), spentTime);
        }
    }

    @Override
    @Transactional
    public void trackServer(Iw4adminApiModel.Server server) {
        serverTrackerRepository.save(ServerTrackEntity.builder()
                .date(new Date())
                .playerCount(server.getCurrentPlayers())
                .serverId(server.getId())
                .mapName(server.getMap().getName())
                .maxPlayerCount(server.getMaxPlayers())
                .percentage(MathUtil.percent(server.getCurrentPlayers(), server.getMaxPlayers()))
                .build());
        addServer(server);
    }

    private synchronized void addServer(Iw4adminApiModel.Server server){
        if(persistedServerIds.contains(server.getId()))
            return;
        try{
            serverRepository.save(ServerEntity.builder()
                    .game(server.getGame())
                    .name(GameUtil.cleanColors(server.getName()))
                    .port(server.getPort())
                    .serverId(server.getId())
                    .build());

        }catch (DuplicateKeyException | ConstraintViolationException ignored){}
        persistedServerIds.add(server.getId());
    }
}
