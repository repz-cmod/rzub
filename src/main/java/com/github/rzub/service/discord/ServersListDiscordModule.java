package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.ServerRepository;
import com.github.rzub.model.ConfigModel;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.listener.DiscordCommandListener;
import com.github.rzub.util.DiscordUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

@DiscordListenerComponent(command = "servers", description = "Lists server names, ids, and game types")
public class ServersListDiscordModule implements DiscordCommandListener {
    private final ServerRepository serverRepository;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    @Autowired
    public ServersListDiscordModule(ConfigModel configModel, ServerRepository serverRepository, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.serverRepository = serverRepository;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }


    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 20);
        MessageEmbed serversList = DiscordUtil.getServersList(serverRepository.findAll());
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(serversList).complete(), 30);
    }
}
