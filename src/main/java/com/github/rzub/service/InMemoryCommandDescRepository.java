package com.github.rzub.service;

import com.github.rzub.database.repository.CommandDescRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryCommandDescRepository implements CommandDescRepository {
    private final Map<String, String> rep = new HashMap<>();

    @Override
    public void addCommand(String command, String description) {
        rep.putIfAbsent(command, description);
    }

    @Override
    public String getDescription(String command) {
        return rep.get(command);
    }

    @Override
    public Map<String, String> getCommandDescriptions() {
        return rep;
    }
}
