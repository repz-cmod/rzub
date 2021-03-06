package com.github.rzub.service;

import com.github.rzub.database.repository.DiscordListenerRepository;
import com.github.rzub.model.CommandAliasModel;
import com.github.rzub.service.listener.DiscordCommandListener;
import com.github.rzub.service.listener.DiscordMessageListener;
import org.springframework.context.annotation.Lazy;
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
    private final CommandAliasModel commandAliasModel;

    public InMemoryDiscordListenerRepository(@Lazy CommandAliasModel commandAliasModel) {
        this.commandAliasModel = commandAliasModel;
    }

    @Override
    public void addCommandListener(String command, Object discordCommandListener) {
        Assert.isTrue(discordCommandListener instanceof DiscordCommandListener, "obj must be instance of DiscordCommandListener");
        commandListenerMap.putIfAbsent(command.toLowerCase(), discordCommandListener);
    }

    @Override
    public synchronized void addMessageListener(Object discordMessageListener) {
        Assert.isTrue(discordMessageListener instanceof DiscordMessageListener, "obj must be instance of DiscordMessageListener");
        if (!discordMessageListeners.contains(discordMessageListener)) {
            discordMessageListeners.add(discordMessageListener);
        }
    }

    @Override
    public Object getListenerOfCommand(String command) {
        return commandListenerMap.get(command.toLowerCase());
    }

    @Override
    public List<Object> getMessageListeners() {
        return discordMessageListeners;
    }

    @Override
    public void onReady() {
        commandAliasModel.getAliases().forEach((cmd, aliases) -> {

            Object o = commandListenerMap.get(cmd);
            if (o != null){
                aliases.forEach(alias -> {
                    commandListenerMap.put(alias, o);
                });
            }
        });
    }


}
