package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.model.ConfigModel;
import com.github.rzub.service.listener.DiscordCommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

@DiscordListenerComponent(command = "forum", description = "Sends the link to forum")
public class ForumLinkDiscordModule implements DiscordCommandListener {
    private final String text;

    @Autowired
    public ForumLinkDiscordModule(ConfigModel configModel) {
        text = "Visit [Forum]("+configModel.getLinks().get("forum")+").";
    }


    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        event.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(new EmbedBuilder().setDescription(text).build())).queue();
    }
}
