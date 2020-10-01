package cmod.repz.application.service.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface DiscordMessageListener {
    void onMessage(GuildMessageReceivedEvent event, String message);
}
