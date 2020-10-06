package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.RepzRandomResponse;
import cmod.repz.application.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent
public class DiscordBotMentionedModule implements DiscordMessageListener {
    private final JDA jda;
    private final RepzRandomResponse repzRandomResponse;

    public DiscordBotMentionedModule(JDA jda, RepzRandomResponse repzRandomResponse) {
        this.jda = jda;
        this.repzRandomResponse = repzRandomResponse;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getMessage().isMentioned(jda.getSelfUser(), Message.MentionType.USER)) {
            try {
                event.getMessage().getChannel().sendMessage(repzRandomResponse.getRandomResponse()).complete();
            }catch (Exception ignored){}
        }
    }
}
