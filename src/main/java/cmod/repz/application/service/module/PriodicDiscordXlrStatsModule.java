package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.util.DiscordUtil;
import cmod.repz.application.util.OffsetLimitPageable;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class PriodicDiscordXlrStatsModule {
    private final String channelId;
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final XlrMw2StatsRepository xlrMw2StatsRepository;
    private final ConfigModel configModel;
    private volatile String messageId;
    private final JDA jda;

    @Autowired
    public PriodicDiscordXlrStatsModule(ConfigModel configModel, XlrBo2StatsRepository xlrBo2StatsRepository, XlrMw2StatsRepository xlrMw2StatsRepository, JDA jda) {
        this.configModel = configModel;
        this.channelId = configModel.getDiscord().getChannels().get("xlrstats");
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
        Assert.notNull(channelId, "ChannelId cant be null");
        Assert.hasText(channelId, "ChannelId cant be empty");
        this.messageId = configModel.getDiscord().getMessages().get("xlrtopstats");
        this.jda = jda;
    }


    @Scheduled(fixedRate = 20000)
    public void updateXlrStats(){
        try {
            TextChannel textChannel = jda.getTextChannelById(channelId);
            MessageEmbed messageEmbed = getXlrTopStatsMessageEmbed();
            if(StringUtils.isEmpty(messageId)){
                Message message = textChannel.sendMessage(messageEmbed).complete();
                messageId = message.getId();
            }else {
                textChannel.editMessageById(Long.parseLong(messageId), messageEmbed).complete();
            }
        }catch (Exception e){
            log.error("Failed to send message to xlr channel. Might be ignorable if doesn't happen again.", e);
        }
    }

    private MessageEmbed getXlrTopStatsMessageEmbed() {
        List<XlrPlayerStatEntity> mw2Stats = xlrMw2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        List<XlrPlayerStatEntity> bo2Stats = xlrBo2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        return DiscordUtil.getTop10XlrResultBothGames(mw2Stats, bo2Stats, configModel);
    }

}
