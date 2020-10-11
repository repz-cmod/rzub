package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.model.event.DiscordPlayerRegisterEvent;
import cmod.repz.application.service.CacheableXlrTopStats;
import cmod.repz.application.service.Top10DiscordPrizeService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscordRegisterXlrTopCheckModule implements ApplicationListener<DiscordPlayerRegisterEvent> {
    private final CacheableXlrTopStats cacheableXlrTopStats;
    private final Top10DiscordPrizeService top10DiscordPrizeService;

    public DiscordRegisterXlrTopCheckModule(CacheableXlrTopStats cacheableXlrTopStats, Top10DiscordPrizeService top10DiscordPrizeService) {
        this.cacheableXlrTopStats = cacheableXlrTopStats;
        this.top10DiscordPrizeService = top10DiscordPrizeService;
    }

    @Override
    public void onApplicationEvent(DiscordPlayerRegisterEvent discordPlayerRegisterEvent) {
        List<XlrPlayerStatEntity> xlrTopStats = cacheableXlrTopStats.getXlrTopStats(discordPlayerRegisterEvent.getGame());
        DiscordUserEntity discordUserEntity = discordPlayerRegisterEvent.getDiscordUserEntity();
        int id = -1;
        if(discordPlayerRegisterEvent.getGame().equals("mw2")) {
            id = Integer.parseInt(discordUserEntity.getB3MW2ClientId());
        }else {
            id = Integer.parseInt(discordUserEntity.getB3BO2ClientId());
        }

        boolean isTop = false;
        for (XlrPlayerStatEntity xlrTopStat : xlrTopStats) {
            if (xlrTopStat.getClient().getId() == id) {
                isTop = true;
                break;
            }
        }

        if(isTop){
            top10DiscordPrizeService.handleTop10Enter(discordPlayerRegisterEvent.getGame(), discordUserEntity);
        }
    }
}
