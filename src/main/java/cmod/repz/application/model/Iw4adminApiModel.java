package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class Iw4adminApiModel {

    @Getter
    @Setter
    @ToString
    public static class Stat {
        private String name;
        private String serverName;
        private String serverGame;
        private int totalSecondsPlayed;
        private int kills;
        private int deaths;
        private int ranking;
    }

    @Getter
    @Setter
    @ToString
    public static class FindApiResult {
        private int totalFoundClients;
        private List<BasicClient> clients;
    }

    @Getter
    @Setter
    @ToString
    public static class BasicClient {
        private int clientId;
        private String xuid;
        private String name;
    }

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
