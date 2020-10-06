package cmod.repz.application.util;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.IW4AdminStatResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.lang.Nullable;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class DiscordUtil {
    private static final DecimalFormat df2 = new DecimalFormat("#.##");

    public static MessageEmbed getTop10XlrResultBothGames(List<XlrPlayerStatEntity> mw2, List<XlrPlayerStatEntity> bo2, ConfigModel configModel){
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("XLR Top Stats")
                .setDescription("This message will update xlr stats periodically.\n")
                .addField("MW2", "Top 10 players from mw2.", false)
                .addField("Player", getLinkedPlayers(mw2, configModel.getXlrMw2Prefix()), true)
                .addField("Skill", getSkills(mw2), true)
                .addField("Ratio", getRatios(mw2), true)
                .addBlankField(false)
                .addField("BO2", "Top 10 players from bo2.", false)
                .addField("Player", getLinkedPlayers(bo2, configModel.getXlrBo2Prefix()), true)
                .addField("Skill", getSkills(bo2), true)
                .addField("Ratio", getRatios(bo2), true)
                .setFooter("Priodic Top XLR Stats")
                .build();
    }

    public static MessageEmbed getTopXlrResult(String game, List<XlrPlayerStatEntity> xlrPlayerStatEntityList){
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("XLRTop stats for " + game)
                .addField("Player", getPlayers(xlrPlayerStatEntityList), true)
                .addField("Skill", getSkills(xlrPlayerStatEntityList), true)
                .addField("Ratio", getRatios(xlrPlayerStatEntityList), true);

        return embedBuilder.build();
    }

    private static String getRatios(List<XlrPlayerStatEntity> xlrPlayerStatEntityList) {
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntityList.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append(df2.format(xlrPlayerStatEntity.getRatio()));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getKills(List<XlrPlayerStatEntity> xlrPlayerStatEntityList) {
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntityList.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append(xlrPlayerStatEntity.getKills());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getDeaths(List<XlrPlayerStatEntity> xlrPlayerStatEntityList) {
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntityList.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append(xlrPlayerStatEntity.getDeaths());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getLinkedPlayers(List<XlrPlayerStatEntity> xlrPlayerStatEntities, String linkPrefix){
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntities.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append("[");
            stringBuilder.append(GameUtil.cleanColors(xlrPlayerStatEntity.getClient().getName()));
            stringBuilder.append("](");
            stringBuilder.append(linkPrefix);
            stringBuilder.append(xlrPlayerStatEntity.getId());
            stringBuilder.append(")");
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getPlayers(List<XlrPlayerStatEntity> xlrPlayerStatEntities){
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntities.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append(GameUtil.cleanColors(xlrPlayerStatEntity.getClient().getName()));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getSkills(List<XlrPlayerStatEntity> xlrPlayerStatEntityList){
        StringBuilder stringBuilder = new StringBuilder();
        xlrPlayerStatEntityList.forEach(xlrPlayerStatEntity -> {
            stringBuilder.append(xlrPlayerStatEntity.getSkill());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    public static MessageEmbed getXlrStatResult(String game, XlrPlayerStatEntity xlrPlayerStatEntity, @Nullable String url){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Ratio: ");
        stringBuilder.append(xlrPlayerStatEntity.getRatio());
        stringBuilder.append("\n");

        stringBuilder.append("Skill: ");
        stringBuilder.append(xlrPlayerStatEntity.getSkill());
        stringBuilder.append("\n");

        stringBuilder.append("Kills: ");
        stringBuilder.append(xlrPlayerStatEntity.getKills());
        stringBuilder.append("\n");

        stringBuilder.append("Deaths: ");
        stringBuilder.append(xlrPlayerStatEntity.getDeaths());
        stringBuilder.append("\n");

        stringBuilder.append("Assists: ");
        stringBuilder.append(xlrPlayerStatEntity.getAssists());
        stringBuilder.append("\n");

        stringBuilder.append("Win Streak: ");
        stringBuilder.append(xlrPlayerStatEntity.getWinStreak());
        stringBuilder.append("\n");

        stringBuilder.append("Lose Streak: ");
        stringBuilder.append(xlrPlayerStatEntity.getLoseStreak());
        stringBuilder.append("\n");

        if(url != null){
            stringBuilder.append("[Open xlrstats page](");
            stringBuilder.append(url);
            stringBuilder.append(")");
        }

        return new EmbedBuilder()
                .setColor(Color.BLACK)
                .setTitle("XLR Stats for " + xlrPlayerStatEntity.getClient().getName() + " in " + game.toLowerCase())
                .setDescription(stringBuilder.toString()).build();
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

    private static String getRanks(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getRank());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getGames(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getGame());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private static String getServers(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(GameUtil.cleanColors(mapRanking.getMap()));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }
}
