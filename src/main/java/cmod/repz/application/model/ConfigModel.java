package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ConfigModel {
    private String iw4adminUrl;
    private Database database;
    private Discord discord;
    private Map<String, Database> xlrDatabase;
    private Map<String, String> messages;

    @Getter
    @Setter
    public static class Database {
        private String url;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Discord {
        private String token;
    }
}
