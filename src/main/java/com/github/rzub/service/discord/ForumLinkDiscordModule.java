package com.github.rzub.service.discord;

import com.github.rzub.model.SettingsModel;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

@DiscordController
public class ForumLinkDiscordModule {
    private final String text;

    @Autowired
    public ForumLinkDiscordModule(SettingsModel settingsModel) {
        text = "Visit [Forum]("+ settingsModel.getLinks().get("forum")+").";
    }

    @DiscordCommand(name = "forum", description = "Sends the link to forum")
    public void onCommand() {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        event.replyEmbeds(new EmbedBuilder().setDescription(text).build()).queue();
    }
}
