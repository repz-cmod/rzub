package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@DiscordListenerComponent(command = "clean", hidden = true)
public class ChannelCleanerDiscordModule implements DiscordCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final ConfigModel configModel;

    public ChannelCleanerDiscordModule(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, ConfigModel configModel) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.configModel = configModel;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        if(!hasAccess(Objects.requireNonNull(event.getMember())))
            return;
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

    private boolean hasAccess(Member member) {
        Map<String, String> roles = configModel.getDiscord().getRoles();
        for (Role role : member.getRoles()) {
            if(role.getId().equals(roles.get("management"))){
                return true;
            }
        }
        return false;
    }
}
