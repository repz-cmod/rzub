package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.repository.repz.ServerRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
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
