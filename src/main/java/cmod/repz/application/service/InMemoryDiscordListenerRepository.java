package cmod.repz.application.service;

import cmod.repz.application.database.repository.DiscordListenerRepository;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.service.listener.DiscordMessageListener;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDiscordListenerRepository implements DiscordListenerRepository {
    private Map<String, DiscordCommandListener> commandListenerMap = new ConcurrentHashMap<>();
    private List<DiscordMessageListener> discordMessageListeners = new ArrayList<>();

    @Override
    public void addCommandListener(String command, DiscordCommandListener discordCommandListener) {
        commandListenerMap.putIfAbsent(command, discordCommandListener);
    }

    @Override
    public synchronized void addMessageListener(DiscordMessageListener discordMessageListener) {
        if (!discordMessageListeners.contains(discordMessageListener)) {
            discordMessageListeners.add(discordMessageListener);
        }
    }

    @Override
    public DiscordCommandListener getListenerOfCommand(String command) {
        return commandListenerMap.get(command);
    }

    @Override
    public List<DiscordMessageListener> getMessageListeners() {
        return discordMessageListeners;
    }
}
