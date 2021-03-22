package cmod.repz.application.service;

import cmod.repz.application.database.AnalyticsDao;
import cmod.repz.application.model.event.ServerStatusEvent;
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
