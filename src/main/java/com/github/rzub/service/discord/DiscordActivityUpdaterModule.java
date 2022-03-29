package com.github.rzub.service.discord;

import com.github.rzub.model.event.ServerStatusEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DiscordActivityUpdaterModule implements ApplicationListener<ServerStatusEvent> {
    private final JDA jda;

    public DiscordActivityUpdaterModule(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onApplicationEvent(ServerStatusEvent serverStatusEvent) {
        AtomicInteger maxPlayers = new AtomicInteger();
        AtomicInteger activePlayers = new AtomicInteger();
        serverStatusEvent.getServerList().forEach(server -> {
            maxPlayers.addAndGet(server.getMaxPlayers());
            activePlayers.addAndGet(server.getCurrentPlayers());
        });

        jda.getPresence().setActivity(Activity.watching(activePlayers + " players out of " + maxPlayers));
    }
}
