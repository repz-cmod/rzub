package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.api.IW4MAdminApiService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;

@DiscordListenerComponent(command = "iwexec", description = "Executes command in iw4madmin", hidden = true)
public class CommandExecuteDiscordModule extends AbstractAuthorizedCommandListener {
    private final CookieRepository cookieRepository;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final IW4MAdminApiService iw4MAdminApiService;


    public CommandExecuteDiscordModule(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, CookieRepository cookieRepository, IW4MAdminApiService iw4MAdminApiService) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.cookieRepository = cookieRepository;
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 20);
        super.onCommand(event, args);
    }

    @Override
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);

        if(args.length < 2){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments.").complete(), 30);
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
}
