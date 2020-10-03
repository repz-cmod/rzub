package cmod.repz.application.database.repository.repz;

import java.util.Map;

public interface CommandDescRepository {
    void addCommand(String command, String description);
    String getDescription(String command);
    Map<String, String> getCommandDescriptions();
}
