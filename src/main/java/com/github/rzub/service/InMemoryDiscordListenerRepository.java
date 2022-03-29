package com.github.rzub.service;

import com.github.rzub.database.repository.DiscordListenerRepository;
import com.github.rzub.service.listener.DiscordMessageListener;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryDiscordListenerRepository implements DiscordListenerRepository {
    private List<Object> discordMessageListeners = new ArrayList<>();

    @Override
    public synchronized void addMessageListener(Object discordMessageListener) {
        Assert.isTrue(discordMessageListener instanceof DiscordMessageListener, "obj must be instance of DiscordMessageListener");
        if (!discordMessageListeners.contains(discordMessageListener)) {
            discordMessageListeners.add(discordMessageListener);
        }
    }

    @Override
    public List<Object> getMessageListeners() {
        return discordMessageListeners;
    }

}
