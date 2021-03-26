package com.github.rzub.service.listener;

import com.github.rzub.annotation.DiscordListenerComponent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface DiscordCommandListener {
    void onCommand(GuildMessageReceivedEvent event, String[] args);
    default String getCommandId(){
        DiscordListenerComponent discordListenerComponent = this.getClass().getAnnotation(DiscordListenerComponent.class);
        if(discordListenerComponent == null)
            throw new RuntimeException("@DiscordListenerComponent is not used on " + this.getClass().getName());

        return discordListenerComponent.command();
    }
}
