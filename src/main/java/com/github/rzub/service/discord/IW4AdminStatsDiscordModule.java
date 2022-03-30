package com.github.rzub.service.discord;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.model.IW4AdminStatResult;
import com.github.rzub.service.CachedIW4MAdminStatsLookupService;
import com.github.rzub.util.DiscordUtil;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Objects;

/*
 * Gets player stats for each game from iw4madmin
 */
@DiscordController
@Slf4j
public class IW4AdminStatsDiscordModule {
    private final CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService;
    private final DiscordUserRepository discordUserRepository;

    public IW4AdminStatsDiscordModule(CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService, DiscordUserRepository discordUserRepository) {
        this.cachedIW4MAdminStatsLookupService = cachedIW4MAdminStatsLookupService;
        this.discordUserRepository = discordUserRepository;
    }

    @DiscordCommand(name = "iwstats", description = "returns player stats in iw4madmin")
    public void onCommand(
            @DiscordParameter(name = "client-id", required = false, description = "(optional) client id") String clientId,
            @DiscordParameter(name = "member", required = false, description = "(optional) another member in server") Member member
            ) {
        try {
            SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
            String userId = null;
            if(clientId != null){
                sendStats(event, clientId);
                return;
            } else if(member != null){
                userId = member.getUser().getId();
            } else {
                userId = Objects.requireNonNull(event.getMember()).getUser().getId();
            }
            DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(userId);
            if(discordUserEntity != null){
                for (IW4MAdminUserEntity iw4MAdminUserEntity : discordUserEntity.getIw4MAdminUserEntities()) {
                    sendStats(event, String.valueOf(iw4MAdminUserEntity.getClientId()));
                }
            }else {
                event.getHook().sendMessage("User has not registered using `!register` command.").queue();
            }
        } catch (Exception e) {
            log.error("Failed to send response for command !iwstats", e);
        }
    }

    public void sendStats(SlashCommandEvent event, String clientId) throws Exception {
        IW4AdminStatResult iw4madminStats = cachedIW4MAdminStatsLookupService.getIW4adminStats(clientId);
        event.getHook().sendMessageEmbeds(DiscordUtil.getEmbedFromIW4AdminStatResult(iw4madminStats, "IW4Admin stats results for client *" + iw4madminStats.getClientId() + "*")).queue();
    }

}
