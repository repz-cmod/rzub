package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.api.IW4MAdminApiService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


@DiscordListenerComponent(command = "iwlogin", description = "Not available", hidden = true)
public class Iw4madminLoginDiscordModule extends AbstractAuthorizedCommandListener {
    private final SettingsModel settingsModel;
    private final CookieRepository cookieRepository;
    private final IW4MAdminApiService iw4MAdminApiService;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    public Iw4madminLoginDiscordModule(CommandAccessService commandAccessService, SettingsModel settingsModel, CookieRepository cookieRepository, IW4MAdminApiService iw4MAdminApiService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.settingsModel = settingsModel;
        this.cookieRepository = cookieRepository;
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }


    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 2);
        super.onCommand(event, args);
    }

    @Override
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {
        if(args.length != 2){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Valid command would be: `!iwlogin <clientId> <password>`. First do a `!rt` in game.").complete(), 30);
            return;
        }
        if (iw4MAdminApiService.logIn(args[0], args[1], cookieRepository)) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Successfully logged in to iw4madmin :)").complete(), 30);
            return;
        }
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Error while logging in to iw4madmin :(").complete(), 30);
    }

}
