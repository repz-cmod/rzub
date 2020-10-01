package cmod.repz.application.service;

import cmod.repz.application.model.IW4AdminStatResult;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.service.api.IW4AdminApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class CacheableIw4adminStatsLookup {
    private final IW4AdminApi iw4AdminApi;

    @Autowired
    public CacheableIw4adminStatsLookup(IW4AdminApi iw4AdminApi) {
        this.iw4AdminApi = iw4AdminApi;
    }

    @Cacheable(cacheNames = "iw4adminStats", key = "#clientId", unless="#result == null")
    public IW4AdminStatResult getIW4adminStats(String clientId) throws Exception {
        log.info("Looking up iw4admin stats for "+ clientId);

        List<IW4AdminStatResult.MapRanking> mapRankings = new ArrayList<>();

        AtomicInteger atomicDeaths = new AtomicInteger();
        AtomicInteger atomicKills = new AtomicInteger();
        List<Iw4adminApiModel.Stat> clientStats = iw4AdminApi.getClientStats(clientId);
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
                .kd(((double) atomicKills.get()) / atomicDeaths.get())
                .rankings(mapRankings)
                .build();

    }

}
