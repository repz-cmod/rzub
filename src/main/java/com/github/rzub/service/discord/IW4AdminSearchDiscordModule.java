package com.github.rzub.service.discord;

import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.service.api.IW4MAdminApiService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

/*
 * Searches in iw4madmin for a player
 */
@DiscordController
@Slf4j
public class IW4AdminSearchDiscordModule {
    private final IW4MAdminApiService iw4MAdminApiService;

    @Autowired
    public IW4AdminSearchDiscordModule(IW4MAdminApiService iw4MAdminApiService) {
        this.iw4MAdminApiService = iw4MAdminApiService;
    }

    @DiscordCommand(name = "iwl", description = "looks up for a player in iw4madmin")
    public void onCommand(@DiscordParameter(name = "name") String searchTerm) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();

        try {
            Iw4madminApiModel.FindApiResult findApiResult = iw4MAdminApiService.findClient(searchTerm);
            StringBuilder stringBuilder = new StringBuilder();
            findApiResult.getClients().forEach(basicClient -> {
                stringBuilder.append(basicClient.getName() + " ("+ basicClient.getClientId() + ")\n");
            });
            event.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.BLACK)
                    .setTitle("IW4Admin search results for *" + searchTerm + "*")
                    .appendDescription(stringBuilder.toString())
                    .build()).queue();
        }catch (Exception e){
            log.error("Failed to send findClient request to iw4madmin");
            event.getHook().sendMessage("Can't process your message atm! try again later.").queue();
        }
    }
}
