package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.RepzRandomResponse;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent(command = "repz", description = "checks bot")
@Slf4j
public class RepzDiscordModule implements DiscordCommandListener {
    private final RepzRandomResponse repzRandomResponse;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public RepzDiscordModule(RepzRandomResponse repzRandomResponse, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.repzRandomResponse = repzRandomResponse;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        Message message = event.getMessage().getChannel().sendMessage(repzRandomResponse.getRandomResponse()).complete();
        discordDelayedMessageRemoverService.scheduleRemove(message, 120);
    }
}
