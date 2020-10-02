package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.model.IW4AdminStatResult;
import cmod.repz.application.service.CacheableIw4adminStatsLookup;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

/*
 * Gets player stats for each game from iw4admin
 */
@DiscordListenerComponent(command = "iwstats")
@Slf4j
public class IW4AdminStatsDiscordModule implements DiscordCommandListener {
    private final CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup;
    private final DiscordUserRepository discordUserRepository;

    public IW4AdminStatsDiscordModule(CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup, DiscordUserRepository discordUserRepository) {
        this.cacheableIw4adminStatsLookup = cacheableIw4adminStatsLookup;
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
                        if (discordUserEntity.getIw4adminMw2ClientId() != null) {
                            sendStats(messageChannel, discordUserEntity.getIw4adminMw2ClientId());
                        }
                        //todo: handle bo2
                    }else {
                        messageChannel.sendMessage("Please provide clientId of iw4admin `!iwstats <clientId>` or register using `!register` command").complete(true);
                    }

                }
            } catch (Exception e) {
                log.error("Failed to send response for command !iwstats", e);
            }
    }

    public void sendStats(MessageChannel messageChannel, String clientId) throws Exception {
        IW4AdminStatResult iw4adminStats = cacheableIw4adminStatsLookup.getIW4adminStats(clientId);
        messageChannel.sendMessage(DiscordUtil.getEmbedFromIW4AdminStatResult(iw4adminStats, "IW4Admin stats results for client *" + iw4adminStats.getClientId() + "*")).complete();
    }

}
