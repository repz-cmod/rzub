package com.github.rzub.service.discord;

import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;


@DiscordController
public class ChannelCleanerDiscordModule {

    @DiscordCommand(name = "clean", description = "Clean messages of channel till <param> message id. Max: 20", ephemeralDiffer = true)
    public void onCommand(@DiscordParameter(name = "message-id") String messageId, @DiscordParameter(name = "max", required = false) Integer max) {
        if (max == null){
            max = 20;
        }
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        List<Message> messageList = event.getChannel().getHistory().retrievePast(max).complete();

        int i = 0;
        while (i < max && i < messageList.size()){
            String latestMessageId = messageList.get(i).getId();
            messageList.get(i).delete().queue();
            if(latestMessageId.equals(messageId)){
                break;
            }
            i++;
        }
        event.getHook().sendMessage("Cleaned "+(i+1)+" messages far from " + messageId).queue();
    }

}
