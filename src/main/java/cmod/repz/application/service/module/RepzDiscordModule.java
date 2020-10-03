package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.RepzRandomResponse;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

@DiscordListenerComponent(command = "repz")
@Slf4j
public class RepzDiscordModule implements DiscordCommandListener {
    private final RepzRandomResponse repzRandomResponse;

    public RepzDiscordModule(RepzRandomResponse repzRandomResponse) {
        this.repzRandomResponse = repzRandomResponse;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        event.getMessage().getChannel().sendMessage(repzRandomResponse.getRandomResponse()).complete();
    }
}
