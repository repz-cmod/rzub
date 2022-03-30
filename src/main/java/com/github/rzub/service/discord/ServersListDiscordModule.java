package com.github.rzub.service.discord;

import com.github.rzub.database.repository.ServerRepository;
import com.github.rzub.util.DiscordUtil;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

@DiscordController
public class ServersListDiscordModule {
    private final ServerRepository serverRepository;

    @Autowired
    public ServersListDiscordModule(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @DiscordCommand(name = "servers", description = "Lists server names, ids, and game types")
    public void onCommand() {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        MessageEmbed serversList = DiscordUtil.getServersList(serverRepository.findAll());
        event.replyEmbeds(serversList).queue();
    }
}
