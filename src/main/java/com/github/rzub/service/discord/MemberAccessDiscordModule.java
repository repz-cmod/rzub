package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

@DiscordListenerComponent
public class MemberAccessDiscordModule implements DiscordMessageListener {
    private final SettingsModel settingsModel;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public MemberAccessDiscordModule(SettingsModel settingsModel, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.settingsModel = settingsModel;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getChannel().getId().equals(settingsModel.getDiscord().getChannels().get("access-grant"))) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 5);
            if(event.getMessage().getContentRaw().toLowerCase().equals(settingsModel.getDiscord().getCustom().get("accept-rules-message").toLowerCase())){
                Role base_member_role = event.getJDA().getRoleById(settingsModel.getDiscord().getRoles().get("base_member_role"));
                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(base_member_role)).complete();
            }
        }
    }
}
