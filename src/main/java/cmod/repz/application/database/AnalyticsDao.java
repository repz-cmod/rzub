package cmod.repz.application.database;

import cmod.repz.application.model.ServerStatusModel;

public interface AnalyticsDao {
    void playerJoined(String serverId, String clientId, String clientName, String trackerId);
    void playerLeft(String serverId, String clientId, String trackerId);
    void trackServer(ServerStatusModel.Server server);
}
