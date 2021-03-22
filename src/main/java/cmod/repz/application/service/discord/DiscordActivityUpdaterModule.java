package cmod.repz.application.service.discord;

import cmod.repz.application.model.event.ServerStatusEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DiscordActivityUpdaterModule implements ApplicationListener<ServerStatusEvent> {
    private final JDA discord;

    public DiscordActivityUpdaterModule(JDA discord) {
        this.discord = discord;
    }

    @Override
    public void onApplicationEvent(ServerStatusEvent serverStatusEvent) {
        AtomicInteger maxPlayers = new AtomicInteger();
        AtomicInteger activePlayers = new AtomicInteger();
        serverStatusEvent.getServerList().forEach(server -> {
            maxPlayers.addAndGet(server.getMaxPlayers());
            activePlayers.addAndGet(server.getCurrentPlayers());
        });

        discord.getPresence().setActivity(Activity.watching(activePlayers + " players out of " + maxPlayers));
    }
}
