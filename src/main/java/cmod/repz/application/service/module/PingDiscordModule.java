package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

@DiscordListenerComponent(command = "repz")
@Slf4j
public class PingDiscordModule implements DiscordCommandListener {

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        log.info("Discord message arrived:" + Arrays.toString(args));
        event.getMessage().getChannel().sendMessage("I'm up! thanks for checking!").complete();
    }
}
