package cmod.repz.application.database;

public interface AnalyticsDao {
    void playerJoined(String serverId, String clientId, String trackerId);
    void playerLeft(String serverId, String clientId, String trackerId);
    void trackServer(String serverId, int playerCount);
}
