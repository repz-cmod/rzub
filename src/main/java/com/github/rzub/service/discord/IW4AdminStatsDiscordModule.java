package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.model.IW4AdminStatResult;
import com.github.rzub.service.CachedIW4MAdminStatsLookupService;
import com.github.rzub.service.listener.DiscordCommandListener;
import com.github.rzub.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

/*
 * Gets player stats for each game from iw4madmin
 */
@DiscordListenerComponent(command = "iwstats", description = "returns player stats in iw4madmin")
@Slf4j
public class IW4AdminStatsDiscordModule implements DiscordCommandListener {
    private final CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService;
    private final DiscordUserRepository discordUserRepository;

    public IW4AdminStatsDiscordModule(CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService, DiscordUserRepository discordUserRepository) {
        this.cachedIW4MAdminStatsLookupService = cachedIW4MAdminStatsLookupService;
        this.discordUserRepository = discordUserRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            MessageChannel messageChannel = event.getMessage().getChannel();
            if(args.length > 0){
                String clientId = args[0];
                sendStats(messageChannel, clientId);
            }else {
                DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId());
                if(discordUserEntity != null){
                    //send mw2 stats
                    if (discordUserEntity.getIw4madminMw2ClientId() != null) {
                        sendStats(messageChannel, discordUserEntity.getIw4madminMw2ClientId());
                    }

                    if (discordUserEntity.getIw4madminBo2ClientId() != null) {
                        sendStats(messageChannel, discordUserEntity.getIw4madminBo2ClientId());
                    }
                }else {
                    messageChannel.sendMessage("Please provide clientId of iw4madmin `!iwstats <clientId>` or register using `!register` command").complete();
                }

            }
        } catch (Exception e) {
            log.error("Failed to send response for command !iwstats", e);
        }
    }

    public void sendStats(MessageChannel messageChannel, String clientId) throws Exception {
        IW4AdminStatResult iw4madminStats = cachedIW4MAdminStatsLookupService.getIW4adminStats(clientId);
        messageChannel.sendMessage(DiscordUtil.getEmbedFromIW4AdminStatResult(iw4madminStats, "IW4Admin stats results for client *" + iw4madminStats.getClientId() + "*")).complete();
    }

}
