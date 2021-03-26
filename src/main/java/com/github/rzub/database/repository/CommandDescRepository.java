package com.github.rzub.database.repository;

import java.util.Map;

public interface CommandDescRepository {
    void addCommand(String command, String description);
    String getDescription(String command);
    Map<String, String> getCommandDescriptions();
}
