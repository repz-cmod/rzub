package com.github.rzub.service;

import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.model.config.IPBConfigModel;

import java.util.Comparator;
import java.util.List;

import static com.github.rzub.config.StaticConfig.IPB_SECONDS_PLAYED_IGNORE;

public abstract class ClientAwareIPBasedAntiEvade implements IPBasedAntiEvade {
    private final DynamicConfigurationProvider dynamicConfigurationProvider;
    private final CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService;

    protected ClientAwareIPBasedAntiEvade(DynamicConfigurationProvider dynamicConfigurationProvider, CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService) {
        this.dynamicConfigurationProvider = dynamicConfigurationProvider;
        this.cachedIW4MAdminStatsLookupService = cachedIW4MAdminStatsLookupService;
    }

    @Override
    public boolean shouldBlock(String clientId, String ip) {
        IPBConfigModel dynamicConfiguration = dynamicConfigurationProvider.getDynamicConfiguration(DynamicConfigurationProvider.Key.IPB);
        if (!dynamicConfiguration.isDateSensitive()) {
            return shouldBlock(ip);
        }

        List<Iw4madminApiModel.Stat> clientStats = cachedIW4MAdminStatsLookupService.getIW4adminStatList(clientId);

        if(clientStats.size() == 0){
            return shouldBlock(ip);
        }

        clientStats.sort(Comparator.comparingInt(Iw4madminApiModel.Stat::getTotalSecondsPlayed));
        if (clientStats.get(clientStats.size() - 1).getTotalSecondsPlayed() > IPB_SECONDS_PLAYED_IGNORE) {
            return false;
        }

        return shouldBlock(ip);
    }
}
