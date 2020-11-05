package cmod.repz.application.database;

import cmod.repz.application.model.Iw4adminApiModel;

public interface AnalyticsDao {
    void playerJoined(Long serverId, Integer clientId, Long trackerId);
    void playerLeft(Long serverId, Integer clientId, Long trackerId);
    void trackServer(Iw4adminApiModel.Server server);
}
