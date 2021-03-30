package com.github.rzub.service;

import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.model.SettingsModel;

import java.util.Comparator;
import java.util.List;

import static com.github.rzub.config.StaticConfig.IPB_SECONDS_PLAYED_IGNORE;

public abstract class ClientAwareIPBasedAntiEvade implements IPBasedAntiEvade {
    private final SettingsModel settingsModel;
    private final CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService;

    protected ClientAwareIPBasedAntiEvade(SettingsModel settingsModel, CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService) {
        this.settingsModel = settingsModel;
        this.cachedIW4MAdminStatsLookupService = cachedIW4MAdminStatsLookupService;
    }

    @Override
    public boolean shouldBlock(String clientId, String ip) {
        if (!settingsModel.getSwitches().getOrDefault("ipb-client-date-sensitive", false)) {
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
