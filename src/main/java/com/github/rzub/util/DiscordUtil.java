package com.github.rzub.util;

import com.github.rzub.database.entity.BasicIPBanInfo;
import com.github.rzub.database.entity.IPRangeBlockEntity;
import com.github.rzub.database.entity.IPRegionBanEntity;
import com.github.rzub.database.entity.ServerEntity;
import com.github.rzub.model.IW4AdminStatResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DiscordUtil {
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");

    public static String argumentsAsOne(String[] args){
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg);
        }
        return stringBuilder.toString();
    }

    public static MessageEmbed getBlockedIpRange(List<IPRangeBlockEntity> ipRangeBlockEntities, int page, int max){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Blocked IP Range List")
                .setDescription("This is list of blocked IP Ranges by RepZ Managers. Each page has 10 results.\nPage: `" +page+"`.\nMax results: " + max +"\n")
                .addField("#", getNumbers(ipRangeBlockEntities), true)
                .addField("range", getRange(ipRangeBlockEntities), true)
                .addField("reason", getReasons(ipRangeBlockEntities), true)
                .build();
    }

    public static MessageEmbed getServersList(List<ServerEntity> serverEntities){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Servers List")
                .addField("game", getGames(serverEntities), true)
                .addField("name", getNames(serverEntities), true)
                .addField("serverId", getIds(serverEntities), true)
                .build();
    }

    public static MessageEmbed getBlockedIpRegion(List<IPRegionBanEntity> ipRangeBlockEntities, int page, int max){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Blocked IP Region List")
                .setDescription("This is list of users blocked by ip region. Each page has 10 results.\nPage: `" +page+"`.\nMax results: " + max +"\n")
                .addField("#", getNumbers(ipRangeBlockEntities), true)
                .addField("reason", getReasons(ipRangeBlockEntities), true)
                .addField("username", getUsernames(ipRangeBlockEntities), true)
                .build();
    }


    public static String getGames(List<ServerEntity> serverEntities){
        StringBuilder stringBuilder = new StringBuilder();
        serverEntities.forEach(serverEntity -> {
            stringBuilder.append(serverEntity.getGame());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }


    public static String getNames(List<ServerEntity> serverEntities){
        StringBuilder stringBuilder = new StringBuilder();
        serverEntities.forEach(serverEntity -> {
            stringBuilder.append(GameUtil.cleanColors(serverEntity.getName().replace(" |  www.cmod.pw", "")).replace("RepZ ", ""));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    public static String getIds(List<ServerEntity> serverEntities){
        StringBuilder stringBuilder = new StringBuilder();
        serverEntities.forEach(serverEntity -> {
            stringBuilder.append(serverEntity.getServerId());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getRange(List<IPRangeBlockEntity> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getStart());
            stringBuilder.append("-");
            stringBuilder.append(ipRangeBlockEntity.getEnd());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getReasons(List<? extends BasicIPBanInfo> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getReason().substring(0, Math.min(8, ipRangeBlockEntity.getReason().length())));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getUsernames(List<? extends BasicIPBanInfo> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getUsername().substring(0, Math.min(8, ipRangeBlockEntity.getUsername().length())));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }


    private static String getEnds(List<IPRangeBlockEntity> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getEnd());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getStarts(List<IPRangeBlockEntity> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getStart());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getNumbers(List<? extends BasicIPBanInfo> ipRangeBlockEntities) {
        StringBuilder stringBuilder = new StringBuilder();
        ipRangeBlockEntities.forEach(ipRangeBlockEntity -> {
            stringBuilder.append(ipRangeBlockEntity.getId());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    public static synchronized MessageEmbed getEmbedFromIW4AdminStatResult(IW4AdminStatResult iw4AdminStatResult, String title){
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLACK)
                .setTitle(title)
                .setDescription("Overall K/D: "+ df2.format(iw4AdminStatResult.getKd()))
                .addField("Server", getServers(iw4AdminStatResult), true)
                .addField("Game", getGames(iw4AdminStatResult), true)
                .addField("Rank", getRanks(iw4AdminStatResult), true);
        return embedBuilder.build();
    }

    private static String getRanks(IW4AdminStatResult iw4madminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4madminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getRank());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getGames(IW4AdminStatResult iw4madminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4madminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getGame());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getServers(IW4AdminStatResult iw4madminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4madminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(GameUtil.cleanColors(mapRanking.getMap()));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }
}
