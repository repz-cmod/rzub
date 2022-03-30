package com.github.rzub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsModel {
    private String iw4madminUrl;
    private Database database;
    private Discord discord;
    private Map<String, String> messages;
    private Map<String, String> links;
    private Modules modules = new Modules(true, true);
    private Security security = new Security();
    private Map<String, JsonNode> custom = new HashMap<>();
    private String clan = "";
    private String domain = "";

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Security {
        private String token = "000000000000";
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Modules {
        private boolean analytics = true;
        private boolean welcome = true;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Discord {
        private String token;
        private Map<String, String> messages = new HashMap<>();
        private Map<String, String> channels = new HashMap<>();
        private Map<String, String> roles = new HashMap<>();
        private Map<String, String> custom = new HashMap<>();
        private String welcome;
    }
}
