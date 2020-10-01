package cmod.repz.application.service;

import cmod.repz.application.database.repository.DiscordListenerRepository;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.service.listener.DiscordMessageListener;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDiscordListenerRepository implements DiscordListenerRepository {
    private Map<String, Object> commandListenerMap = new ConcurrentHashMap<>();
    private List<Object> discordMessageListeners = new ArrayList<>();

    @Override
    public void addCommandListener(String command, Object discordCommandListener) {
        Assert.isTrue(discordCommandListener instanceof DiscordCommandListener, "obj must be instance of DiscordCommandListener");
        commandListenerMap.putIfAbsent(command.toLowerCase(), discordCommandListener);
    }

    @Override
    public synchronized void addMessageListener(Object discordMessageListener) {
        Assert.isTrue(discordMessageListener instanceof DiscordCommandListener, "obj must be instance of DiscordMessageListener");
        if (!discordMessageListeners.contains(discordMessageListener)) {
            discordMessageListeners.add(discordMessageListener);
        }
    }

    @Override
    public Object getListenerOfCommand(String command) {
        return commandListenerMap.get(command);
    }

    @Override
    public List<Object> getMessageListeners() {
        return discordMessageListeners;
    }
}
