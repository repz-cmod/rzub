package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigModel {
    private String iw4adminUrl;
    private Database database;
    private Discord discord;

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
