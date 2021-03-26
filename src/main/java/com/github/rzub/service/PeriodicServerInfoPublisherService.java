package com.github.rzub.service;

import com.github.rzub.model.event.ServerStatusEvent;
import com.github.rzub.service.api.IW4MAdminApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PeriodicServerInfoPublisherService {
    private final IW4MAdminApiService iw4MAdminApiService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PeriodicServerInfoPublisherService(IW4MAdminApiService iw4MAdminApiService, ApplicationEventPublisher applicationEventPublisher) {
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(fixedRate = 60000)
    @Async
    public void runTask(){
        log.debug("Running server tracking task");
        applicationEventPublisher.publishEvent(new ServerStatusEvent(this, iw4MAdminApiService.getServerList()));
    }

}
