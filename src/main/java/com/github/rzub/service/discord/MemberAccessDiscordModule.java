package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.model.ConfigModel;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

@DiscordListenerComponent
public class MemberAccessDiscordModule implements DiscordMessageListener {
    private final ConfigModel configModel;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public MemberAccessDiscordModule(ConfigModel configModel, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.configModel = configModel;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals(configModel.getDiscord().getChannels().get("access-grant"))) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 5);
            if(event.getMessage().getContentRaw().toLowerCase().equals(configModel.getDiscord().getCustom().get("accept-rules-message").toLowerCase())){
                Role base_member_role = event.getJDA().getRoleById(configModel.getDiscord().getRoles().get("base_member_role"));
                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(base_member_role)).complete();
            }
        }
    }
}
