package cmod.repz.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConfigModel {
    private String iw4adminUrl;
    private Database database;
    private Discord discord;
    private Map<String, String> messages;
    private Map<String, String> links;
    private Modules modules = new Modules(true, true);

    @Getter
    @Setter
    public static class Database {
        private String url;
        private String username;
        private String password;
        private String dialect = "com.mysql.jdbc.Driver";
        private String hbm2ddl = "none";
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Modules {
        private boolean tracker = true;
        private boolean welcome = true;
    }

    @Getter
    @Setter
    public static class Discord {
        private String token;
        private Map<String, String> messages;
        private Map<String, String> channels;
        private Map<String, String> roles;
        private Map<String, String> custom;
        private List<String> ipb;
        private String welcome;
    }
}
