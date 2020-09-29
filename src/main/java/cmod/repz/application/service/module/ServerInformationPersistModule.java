package cmod.repz.application.service.module;

import cmod.repz.application.database.AnalyticsDao;
import cmod.repz.application.model.event.ServerStatusEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerInformationPersistModule implements ApplicationListener<ServerStatusEvent> {
    private final AnalyticsDao analyticsDao;

    public ServerInformationPersistModule(AnalyticsDao analyticsDao) {
        this.analyticsDao = analyticsDao;
    }

    @Override
    public void onApplicationEvent(ServerStatusEvent serverStatusEvent) {
        serverStatusEvent.getServerList().forEach(analyticsDao::trackServer);
    }
}
