package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConfigModel {
    private String iw4adminUrl;
    private Database database;
    private Discord discord;
    private Map<String, Database> xlrDatabase;
    private Map<String, String> messages;
    private String xlrMw2Prefix;
    private String xlrBo2Prefix;
    private String xlrBf3Prefix;
    private Map<String, String> links;

    @Getter
    @Setter
    public static class Database {
        private String url;
        private String username;
        private String password;
        private String hbm2ddl = "none";
    }

    @Getter
    @Setter
    public static class Discord {
        private String token;
        private Map<String, String> messages;
        private Map<String, String> channels;
        private Map<String, String> roles;
        private List<String> ipb;
        private String welcome;
    }
}
