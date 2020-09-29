package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class ServerStatusModel {

    @Getter
    @Setter
    @ToString
    public static class Player {
        private String name;
        private String score;
        private int ping;
        private int connectionTime;
    }

    @Getter
    @Setter
    @ToString
    public static class Server {
        private long id;
        private String name;
        private int maxPlayers;
        private int currentPlayers;
        private String game;
        private int port;
        private Map map;
        private List<Player> players;
    }

    @Getter
    @Setter
    @ToString
    public static class Map {
        private String name;
        private String alias;
    }
}
