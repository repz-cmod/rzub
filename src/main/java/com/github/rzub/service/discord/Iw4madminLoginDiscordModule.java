package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.model.ConfigModel;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.api.IW4MAdminApiService;
import com.github.rzub.service.listener.DiscordCommandListener;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;


@AllArgsConstructor
@DiscordListenerComponent(command = "iwlogin", description = "Not available", hidden = true)
public class Iw4madminLoginDiscordModule implements DiscordCommandListener {
    private final ConfigModel configModel;
    private final CookieRepository cookieRepository;
    private final IW4MAdminApiService iw4MAdminApiService;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 2);
        if(args.length != 2){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Valid command would be: `!iwlogin <clientId> <password>`. First do a `!rt` in game.").complete(), 30);
            return;
        }
        if(!hasAccess(Objects.requireNonNull(event.getMember()))){
            return;
        }
        if (iw4MAdminApiService.logIn(args[0], args[1], cookieRepository)) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Successfully logged in to iw4madmin :)").complete(), 30);
            return;
        }
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Error while logging in to iw4madmin :(").complete(), 30);
    }

    private boolean hasAccess(Member member){
        return configModel.getDiscord().getIpb().contains(member.getId());
    }
}
