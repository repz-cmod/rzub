package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordMessageListener;
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
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 2); //todo: 60 -> 10
            if(event.getMessage().getContentRaw().toLowerCase().equals(configModel.getDiscord().getCustom().get("accept-rules-message").toLowerCase())){
                Role rzmember = event.getJDA().getRoleById(configModel.getDiscord().getRoles().get("rzmember"));
                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(rzmember)).complete();
            }
        }
    }
}