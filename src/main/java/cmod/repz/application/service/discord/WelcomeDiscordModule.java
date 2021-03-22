package cmod.repz.application.service.discord;

import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.event.DiscordMemberJoinEVent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@Slf4j
public class WelcomeDiscordModule implements ApplicationListener<DiscordMemberJoinEVent> {
    private final ConfigModel configModel;

    @Autowired
    public WelcomeDiscordModule(ConfigModel configModel) {
        this.configModel = configModel;
    }

    @Override
    public void onApplicationEvent(DiscordMemberJoinEVent discordMemberJoinEVent) {
        if (!configModel.getModules().isWelcome())
            return;
        String user = discordMemberJoinEVent.getGuildMemberJoinEvent().getUser().getAsMention();
        String welcome = configModel.getDiscord().getWelcome();
        String result = welcome.replace("$user", user).replace("$infoLink", configModel.getLinks().get("infoChannel")).replace("$rulesToAccess", configModel.getLinks().get("rulesToAccessChannel"));
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
