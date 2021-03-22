package cmod.repz.application.service.discord;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.RandomResponseService;
import cmod.repz.application.service.listener.DiscordMessageListener;
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
