package com.github.rzub.service.discord;

import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;


@DiscordController
public class ChannelCleanerDiscordModule {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public ChannelCleanerDiscordModule(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @DiscordCommand(name = "clean", description = "Clean messages of channel till <param> message id. Max: 20")
    public void onCommand(@DiscordParameter(name = "message-id") String messageId, @DiscordParameter(name = "max", required = false) Integer max) {
        if (max == null){
            max = 20;
        }
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        List<Message> messageList = event.getChannel().getHistory().retrievePast(max).complete();

        int i = 0;
        while (i < 20 && i < messageList.size()){
            String latestMessageId = messageList.get(i).getId();
            messageList.get(i).delete().complete();
            if(latestMessageId.equals(messageId)){
                break;
            }
            i++;
        }
        discordDelayedMessageRemoverService.scheduleRemove(event.getChannel().sendMessage("Cleaned "+(i+1)+" messages far from " + messageId).complete(), 10);
    }

}
