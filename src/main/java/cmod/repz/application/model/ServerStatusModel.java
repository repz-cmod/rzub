package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;

public class ServerStatusModel {


    @Getter
    @Setter
    public static class Server {
        private long id;
        private String name;
        private int maxPlayers;
        private int currentPlayers;
        private String game;
        private int port;
        private Map map;
    }

    @Getter
    @Setter
    public static class Map {
        private String name;
        private String alias;
    }
}
