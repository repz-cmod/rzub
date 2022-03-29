package com.github.rzub.database.repository;

import java.util.List;

public interface DiscordListenerRepository {
    void addMessageListener(Object discordMessageListener);
    List<Object> getMessageListeners();
}
