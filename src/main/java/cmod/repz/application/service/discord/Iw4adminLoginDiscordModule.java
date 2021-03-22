package cmod.repz.application.service.discord;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.repository.repz.CookieRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.api.IW4AdminApi;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;


@AllArgsConstructor
@DiscordListenerComponent(command = "iwlogin", description = "Not available", hidden = true)
public class Iw4adminLoginDiscordModule implements DiscordCommandListener {
    private final ConfigModel configModel;
    private final CookieRepository cookieRepository;
    private final IW4AdminApi iw4AdminApi;
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
        if (iw4AdminApi.logIn(args[0], args[1], cookieRepository)) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Successfully logged in to iw4admin :)").complete(), 30);
            return;
        }
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Error while logging in to iw4admin :(").complete(), 30);
    }

    private boolean hasAccess(Member member){
        return configModel.getDiscord().getIpb().contains(member.getId());
    }
}
