package cmod.repz.application.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class ConfigModel {
    private String iw4madminUrl;
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
        private String driver = "com.mysql.jdbc.Driver";
        private String dialect = "org.hibernate.dialect.MySQL5InnoDBDialect";
        private String hbm2ddl = "none";
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Modules {
        private boolean analytics = true;
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
