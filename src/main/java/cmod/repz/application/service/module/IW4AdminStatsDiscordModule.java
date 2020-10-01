package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.IW4AdminStatResult;
import cmod.repz.application.service.CacheableIw4adminStatsLookup;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.GameUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@DiscordListenerComponent(command = "iwstat")
@Slf4j
public class IW4AdminStatsDiscordModule implements DiscordCommandListener {
    private final CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup;
    private final DecimalFormat df2 = new DecimalFormat("#.##");

    public IW4AdminStatsDiscordModule(CacheableIw4adminStatsLookup cacheableIw4adminStatsLookup) {
        this.cacheableIw4adminStatsLookup = cacheableIw4adminStatsLookup;
        df2.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
            try {
                MessageChannel messageChannel = event.getMessage().getChannel();
                if(args.length == 0){
                    Message message = messageChannel.sendMessage("Please provide clientId of iw4admin").complete(true);
                }else {
                    String clientId = args[0];
                    try {
                        IW4AdminStatResult iw4adminStats = cacheableIw4adminStatsLookup.getIW4adminStats(clientId);


                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setColor(Color.BLACK)
                                .setTitle("IW4Admin stats results for client *" + clientId + "*")
                                .setDescription("Overall K/D: "+ df2.format(iw4adminStats.getKd()))
                                .addField("Server", getServers(iw4adminStats), true)
                                .addField("Game", getGames(iw4adminStats), true)
                                .addField("Rank", getRanks(iw4adminStats), true);


                        messageChannel.sendMessage(embedBuilder.build()).complete();
                    }catch (Exception e){
                        log.error("Failed to lookup client", e);
                        Message message = messageChannel.sendMessage("Can't process your message atm! try again later.").complete();
                    }
                }
            } catch (Exception e) {
                log.error("Failed to send response for command !iwl", e);
            }
    }

    private String getRanks(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getRank());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private String getGames(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(mapRanking.getGame());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

    private String getServers(IW4AdminStatResult iw4adminStats) {
        StringBuilder stringBuilder = new StringBuilder();
        iw4adminStats.getRankings().forEach(mapRanking -> {
            stringBuilder.append(GameUtil.cleanColors(mapRanking.getMap()));
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }

}
