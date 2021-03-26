package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.api.IW4MAdminApiService;
import com.github.rzub.service.listener.DiscordCommandListener;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

@DiscordListenerComponent(command = "iwexec", description = "Executes command in iw4madmin", hidden = true)
public class CommandExecuteDiscordModule implements DiscordCommandListener {
    private final CookieRepository cookieRepository;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final IW4MAdminApiService iw4MAdminApiService;

    private final SettingsModel settingsModel;

    public CommandExecuteDiscordModule(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, CookieRepository cookieRepository, IW4MAdminApiService iw4MAdminApiService, SettingsModel settingsModel) {
        this.cookieRepository = cookieRepository;
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.settingsModel = settingsModel;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @SneakyThrows
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);

        if (!hasAccess(event.getMember())){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("You dont have access!").complete(), 30);
            return;
        }

        if(args.length < 2){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments. Try `!ipb2 help`. This command is only available for admins").complete(), 30);
            return;
        }

        IW4MAdminApiService.CommandResponse commandResponse = iw4MAdminApiService.execute(args[0], String.join(" ", Arrays.copyOfRange(args, 2, args.length)), cookieRepository);
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Success: `" + commandResponse.isSuccess() +  "` | status: `" + commandResponse.getStatus() + "` | body: `" + getPartialBody(commandResponse.getBody()) + "`").complete(), 30);
    }

    private String getPartialBody(String body){
        if(body.length() < 100)
            return body;
        else {
            return body.substring(0, 100) + " ...";
        }
    }

    private boolean hasAccess(Member member) {
        String management = settingsModel.getDiscord().getRoles().get("management");
        for (Role role : member.getRoles()) {
            if(role.getId().equals(management))
                return true;
        }
        return false;
    }
}
