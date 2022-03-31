package com.github.rzub.database;

import com.github.rzub.model.Iw4madminApiModel;

public interface AnalyticsDao {
    void trackServer(Iw4madminApiModel.Server server);
}
