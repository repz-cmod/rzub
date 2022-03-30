package com.github.rzub.service.discord;

import com.github.rzub.service.api.IW4MAdminApiService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@DiscordController
public class Iw4madminLoginDiscordModule {
    private final IW4MAdminApiService iw4MAdminApiService;

    public Iw4madminLoginDiscordModule(IW4MAdminApiService iw4MAdminApiService) {
        this.iw4MAdminApiService = iw4MAdminApiService;
    }


    @DiscordCommand(name = "iwlogin", description = "Log the bot into iw4madmin")
    public void onCommand(@DiscordParameter(name="client-id") String clientId, @DiscordParameter(name="password") String password) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        if (iw4MAdminApiService.logIn(clientId, password)){
            event.reply("Successfully logged in to iw4madmin :)").queue();
        }else {
            event.reply("Error while logging in to iw4madmin :(").queue();
        }
    }

}
