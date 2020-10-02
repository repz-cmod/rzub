package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.DiscordUserEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.model.IW4AdminStatResult;
import cmod.repz.application.service.CacheableIw4adminStatsLookup;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

/*
 * Gets registered player stats from iw4admin
 */
@DiscordListenerComponent(command = "mystat")
@Slf4j
public class IW4AdminUserStatsDiscordModule implements DiscordCommandListener {
    private final CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup;
    private final DiscordUserRepository discordUserRepository;

    public IW4AdminUserStatsDiscordModule(CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup, DiscordUserRepository discordUserRepository) {
        this.cacheableIw4adminStatsLookup = cacheableIw4adminStatsLookup;
        this.discordUserRepository = discordUserRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            MessageChannel messageChannel = event.getMessage().getChannel();
            try {
                DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId());
                if(discordUserEntity == null){
                    Message message = messageChannel.sendMessage("You need to register first! use `!register` command").complete();
                }else {
                    IW4AdminStatResult iw4adminStats = cacheableIw4adminStatsLookup.getIW4adminStats(discordUserEntity.getIw4adminClientId());
                    messageChannel.sendMessage(DiscordUtil.getEmbedFromIW4AdminStatResult(iw4adminStats, "IW4Admin stats results for player *" + discordUserEntity.getClientName() + "*")).complete();
                }
            }catch (Exception e){
                log.error("Failed to lookup client", e);
                Message message = messageChannel.sendMessage("Can't process your message atm! try again later.").complete();
            }

        } catch (Exception e) {
            log.error("Failed to send response for command !iwl", e);
        }
    }



}
