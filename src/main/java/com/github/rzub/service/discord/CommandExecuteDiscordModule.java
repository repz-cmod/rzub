package com.github.rzub.service.discord;

import com.github.rzub.service.api.IW4MAdminApiService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@DiscordController
public class CommandExecuteDiscordModule {
    private final IW4MAdminApiService iw4MAdminApiService;


    public CommandExecuteDiscordModule(IW4MAdminApiService iw4MAdminApiService) {
        this.iw4MAdminApiService = iw4MAdminApiService;
    }

    @DiscordCommand(name = "iwexec", description = "Executes command in iw4madmin")
    public void onCommand(@DiscordParameter(name = "server-id") String serverId, @DiscordParameter(name="command") String command) {
        IW4MAdminApiService.CommandResponse commandResponse = iw4MAdminApiService.execute(serverId, command);
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        event.reply("Success: `" + commandResponse.isSuccess() +  "` | status: `" + commandResponse.getStatus() + "` | body: `" + getPartialBody(commandResponse.getBody()) + "`").queue();
    }

    private String getPartialBody(String body){
        if(body.length() < 100)
            return body;
        else {
            return body.substring(0, 100) + " ...";
        }
    }
}
