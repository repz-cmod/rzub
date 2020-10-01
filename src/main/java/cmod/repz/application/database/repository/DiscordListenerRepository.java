package cmod.repz.application.database.repository;

import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.service.listener.DiscordMessageListener;

import java.util.List;

public interface DiscordListenerRepository {
    void addCommandListener(String command, DiscordCommandListener discordCommandListener);
    void addMessageListener(DiscordMessageListener discordMessageListener);
    DiscordCommandListener getListenerOfCommand(String command);
    List<DiscordMessageListener> getMessageListeners();
}
