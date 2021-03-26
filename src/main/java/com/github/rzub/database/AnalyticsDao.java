package com.github.rzub.database;

import com.github.rzub.model.Iw4adminApiModel;

public interface AnalyticsDao {
    void playerJoined(Long serverId, Integer clientId, Long trackerId);
    void playerLeft(Long serverId, Integer clientId, Long trackerId);
    void trackServer(Iw4adminApiModel.Server server);
}
