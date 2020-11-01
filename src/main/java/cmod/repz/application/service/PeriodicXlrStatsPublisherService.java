package cmod.repz.application.service;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.bf3.bo2.XlrBf3StatsRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.model.event.XlrTopEvent;
import cmod.repz.application.util.OffsetLimitPageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodicXlrStatsPublisherService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final XlrMw2StatsRepository xlrMw2StatsRepository;
    private final XlrBf3StatsRepository xlrBf3StatsRepository;

    public PeriodicXlrStatsPublisherService(ApplicationEventPublisher applicationEventPublisher, XlrBo2StatsRepository xlrBo2StatsRepository, XlrMw2StatsRepository xlrMw2StatsRepository, XlrBf3StatsRepository xlrBf3StatsRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
        this.xlrBf3StatsRepository = xlrBf3StatsRepository;
    }

    @Scheduled(fixedRate = 20000L)
    public void runTask(){
        List<XlrPlayerStatEntity> mw2Stats = xlrMw2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        List<XlrPlayerStatEntity> bo2Stats = xlrBo2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        List<XlrPlayerStatEntity> bf3stats = xlrBf3StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        applicationEventPublisher.publishEvent(new XlrTopEvent(this, mw2Stats, bo2Stats, bf3stats));
    }
}
