package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.RepzRandomResponse;
import cmod.repz.application.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent
public class BotMentionedDiscordModule implements DiscordMessageListener {
    private final JDA jda;
    private final RepzRandomResponse repzRandomResponse;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public BotMentionedDiscordModule(JDA jda, RepzRandomResponse repzRandomResponse, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.jda = jda;
        this.repzRandomResponse = repzRandomResponse;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getMessage().isMentioned(jda.getSelfUser(), Message.MentionType.USER)) {
            try {
                Message message = event.getMessage().getChannel().sendMessage(repzRandomResponse.getRandomResponse()).complete();
                discordDelayedMessageRemoverService.scheduleRemove(message, 120);
            }catch (Exception ignored){}
        }
    }
}
