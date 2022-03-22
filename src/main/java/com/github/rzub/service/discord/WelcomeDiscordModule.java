package com.github.rzub.service.discord;

import com.github.rzub.model.SettingsModel;
import com.github.rzub.model.event.DiscordMemberJoinEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@Slf4j
public class WelcomeDiscordModule implements ApplicationListener<DiscordMemberJoinEvent> {
    private final SettingsModel settingsModel;

    @Autowired
    public WelcomeDiscordModule(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }

    @Override
    public void onApplicationEvent(DiscordMemberJoinEvent discordMemberJoinEVent) {
        if (!settingsModel.getModules().isWelcome())
            return;
        String user = discordMemberJoinEVent.getGuildMemberJoinEvent().getUser().getAsMention();
        String welcome = settingsModel.getDiscord().getWelcome();
        String result = welcome.replace("$user", user).replace("$infoLink", settingsModel.getLinks().get("infoChannel")).replace("$rulesToAccess", settingsModel.getLinks().get("rulesToAccessChannel"));
        try {
            discordMemberJoinEVent.getGuildMemberJoinEvent().getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setDescription(result)
                    .build())).queue();
        }catch (Exception e){
            log.error("Failed to send welcome message", e);
        }
    }
}
