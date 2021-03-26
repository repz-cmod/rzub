package com.github.rzub.database;

import com.github.rzub.model.Iw4madminApiModel;

public interface AnalyticsDao {
    void playerJoined(Long serverId, Integer clientId, Long trackerId);
    void playerLeft(Long serverId, Integer clientId, Long trackerId);
    void trackServer(Iw4madminApiModel.Server server);
}
