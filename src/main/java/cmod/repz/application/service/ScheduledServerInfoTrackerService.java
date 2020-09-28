package cmod.repz.application.service;

import cmod.repz.application.database.AnalyticsDao;
import cmod.repz.application.service.api.IW4AdminApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScheduledServerInfoTrackerService {
    private final AnalyticsDao analyticsDao;
    private final IW4AdminApi iw4AdminApi;

    public ScheduledServerInfoTrackerService(AnalyticsDao analyticsDao, IW4AdminApi iw4AdminApi) {
        this.analyticsDao = analyticsDao;
        this.iw4AdminApi = iw4AdminApi;
    }

    @Scheduled(fixedRate = 30000)
    @Async
    public void runTask(){
        log.debug("Running server tracking task");
        iw4AdminApi.getServerList().forEach(analyticsDao::trackServer);
    }

}
