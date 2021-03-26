package com.github.rzub.service;

import com.github.rzub.database.AnalyticsDao;
import com.github.rzub.model.event.ServerStatusEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class ServerInformationPersistService implements ApplicationListener<ServerStatusEvent> {
    private final AnalyticsDao analyticsDao;

    public ServerInformationPersistService(AnalyticsDao analyticsDao) {
        this.analyticsDao = analyticsDao;
    }

    @Override
    public void onApplicationEvent(ServerStatusEvent serverStatusEvent) {
        serverStatusEvent.getServerList().forEach(analyticsDao::trackServer);
    }
}
