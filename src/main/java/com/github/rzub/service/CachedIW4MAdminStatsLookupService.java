package com.github.rzub.service;

import com.github.rzub.model.IW4AdminStatResult;
import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.service.api.IW4MAdminApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CachedIW4MAdminStatsLookupService {
    private final IW4MAdminApiService iw4MAdminApiService;

    @Autowired
    public CachedIW4MAdminStatsLookupService(IW4MAdminApiService iw4MAdminApiService) {
        this.iw4MAdminApiService = iw4MAdminApiService;
    }

    @Cacheable(cacheNames = "iw4madminStatList", key = "#clientId", unless="#result == null || #result.size() == 0")
    public List<Iw4madminApiModel.Stat> getIW4adminStatList(String clientId) {
        return iw4MAdminApiService.getClientStats(clientId);
    }

    @Cacheable(cacheNames = "iw4madminStats", key = "#clientId", unless="#result == null")
    public IW4AdminStatResult getIW4adminStats(String clientId) throws Exception {
        log.info("Looking up iw4madmin stats for "+ clientId);

        List<IW4AdminStatResult.MapRanking> mapRankings = new ArrayList<>();

        AtomicInteger atomicDeaths = new AtomicInteger();
        AtomicInteger atomicKills = new AtomicInteger();
        List<Iw4madminApiModel.Stat> clientStats = iw4MAdminApiService.getClientStats(clientId);
        clientStats.forEach(stat -> {
            atomicDeaths.addAndGet(stat.getDeaths());
            atomicKills.addAndGet(stat.getKills());
            mapRankings.add(IW4AdminStatResult.MapRanking.builder()
                    .game(stat.getServerGame())
                    .map(stat.getServerName())
                    .rank(stat.getRanking())
                    .build());
        });

        return IW4AdminStatResult.builder()
                .clientId(clientId)
                .kd(((double) atomicKills.get()) / atomicDeaths.get())
                .rankings(mapRankings)
                .build();

    }

}
