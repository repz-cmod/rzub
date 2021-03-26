package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

@DiscordListenerComponent(command = "clean", hidden = true)
public class ChannelCleanerDiscordModule extends AbstractAuthorizedCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public ChannelCleanerDiscordModule(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 20);
        super.onCommand(event, args);
    }

    @Override
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {
        if(args.length > 0){
            String messageId = args[0];

            List<Message> messageList = event.getChannel().getHistory().retrievePast(20).complete();

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
}
