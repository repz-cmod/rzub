package cmod.repz.application.service.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface DiscordCommandListener {
    void onCommand(GuildMessageReceivedEvent event, String[] args);
}