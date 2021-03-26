package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.RandomResponseService;
import com.github.rzub.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent
public class BotMentionedDiscordModule implements DiscordMessageListener {
    private final JDA jda;
    private final RandomResponseService randomResponseService;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public BotMentionedDiscordModule(JDA jda, RandomResponseService randomResponseService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.jda = jda;
        this.randomResponseService = randomResponseService;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getMessage().isMentioned(jda.getSelfUser(), Message.MentionType.USER)) {
            try {
                Message message = event.getMessage().getChannel().sendMessage(randomResponseService.getRandomResponse()).complete();
                discordDelayedMessageRemoverService.scheduleRemove(message, 120);
            }catch (Exception ignored){}
        }
    }
}
