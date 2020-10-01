package cmod.repz.application.database.repository;

import java.util.List;

public interface DiscordListenerRepository {
    void addCommandListener(String command, Object discordCommandListener);
    void addMessageListener(Object discordMessageListener);
    Object getListenerOfCommand(String command);
    List<Object> getMessageListeners();
}
