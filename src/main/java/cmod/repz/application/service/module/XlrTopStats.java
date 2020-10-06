package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.service.CacheableXlrTopStats;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DiscordListenerComponent(command = "xlrtop", description = "Returns list of top xlr players of a game")
@Slf4j
public class XlrTopStats implements DiscordCommandListener {
    private final CacheableXlrTopStats cacheableXlrTopStats;

    @Autowired
    public XlrTopStats(CacheableXlrTopStats cacheableXlrTopStats) {
        this.cacheableXlrTopStats = cacheableXlrTopStats;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        MessageChannel messageChannel = event.getMessage().getChannel();

        if(args.length < 1){
            messageChannel.sendMessage("Please provide game parameter. Like: `!xlrtop <game>`").complete();
        }else {
            try {
                String game = args[0].toLowerCase();
                if(!game.equals("mw2") && !game.equals("bo2"))
                    return;
                List<XlrPlayerStatEntity> xlrTopStats = cacheableXlrTopStats.getXlrTopStats(game);
                MessageEmbed topXlrResult = DiscordUtil.getTopXlrResult(game, xlrTopStats);
                messageChannel.sendMessage(topXlrResult).complete();
            }catch (Exception e){
                log.error("Failed to process !xlrtop", e);
            }
        }
    }


}
