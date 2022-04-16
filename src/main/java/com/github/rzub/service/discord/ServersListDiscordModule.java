package com.github.rzub.service.discord;

import com.github.rzub.service.CachedIW4MAdminStatsLookupService;
import com.github.rzub.util.DiscordUtil;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

@DiscordController
public class ServersListDiscordModule {
    private final CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService;

    @Autowired
    public ServersListDiscordModule(CachedIW4MAdminStatsLookupService cachedIW4MAdminStatsLookupService) {
        this.cachedIW4MAdminStatsLookupService = cachedIW4MAdminStatsLookupService;
    }

    @DiscordCommand(name = "servers", description = "Lists server names, ids, and game types")
    public void onCommand() {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        MessageEmbed serversList = DiscordUtil.getServersList(cachedIW4MAdminStatsLookupService.getServerList());
        event.getHook().sendMessageEmbeds(serversList).queue();
    }
}
