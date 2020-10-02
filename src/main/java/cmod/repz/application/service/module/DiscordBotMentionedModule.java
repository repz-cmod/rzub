package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent
public class DiscordBotMentionedModule implements DiscordMessageListener {
    private final JDA jda;

    public DiscordBotMentionedModule(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if (event.getMessage().isMentioned(jda.getSelfUser())) {
            try {
                event.getMessage().getChannel().sendMessage("I'm glad you are talking to me! Or about me! Either ways, I can't understand shit :(").complete();
            }catch (Exception ignored){}
        }
    }
}
