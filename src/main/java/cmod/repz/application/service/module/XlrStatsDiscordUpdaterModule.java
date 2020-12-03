package cmod.repz.application.service.module;

import cmod.repz.application.config.DiscordStateHolder;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.event.XlrTopEvent;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class XlrStatsDiscordUpdaterModule implements ApplicationListener<XlrTopEvent> {
    private final String channelId;
    private final ConfigModel configModel;
    private volatile String messageId;
    private final JDA jda;

    @Autowired
    public XlrStatsDiscordUpdaterModule(ConfigModel configModel, JDA jda) {
        this.configModel = configModel;
        this.channelId = configModel.getDiscord().getChannels().get("xlrstats");
        Assert.notNull(channelId, "ChannelId cant be null");
        Assert.hasText(channelId, "ChannelId cant be empty");
        this.messageId = configModel.getDiscord().getMessages().get("xlrtopstats");
        this.jda = jda;
    }

    private MessageEmbed getXlrTopStatsMessageEmbed(XlrTopEvent xlrTopEvent) {
        return DiscordUtil.getTop10XlrResultAllGames(xlrTopEvent.getMw2Stats(), xlrTopEvent.getBo2Stats(), xlrTopEvent.getBf3stats(), configModel);
    }

    @Override
    public void onApplicationEvent(@NotNull XlrTopEvent xlrTopEvent) {
        if(!DiscordStateHolder.isReady())
            return;
        try {
            TextChannel textChannel = jda.getTextChannelById(channelId);
            MessageEmbed messageEmbed = getXlrTopStatsMessageEmbed(xlrTopEvent);
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
}
