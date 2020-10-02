package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/*
 * Gets registered player stats from iw4admin
 */
@DiscordListenerComponent(command = "xlrstat")
@Slf4j
public class XlrMw2StatsDiscordModule implements DiscordCommandListener {
    private final XlrMw2StatsRepository xlrMw2StatsRepository;

    public XlrMw2StatsDiscordModule(XlrMw2StatsRepository xlrMw2StatsRepository) {
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            MessageChannel messageChannel = event.getMessage().getChannel();
            if(args.length < 2){
                messageChannel.sendMessage("Please provide clientId and game. Like: `!xlrstat <game> <clientId>`").complete();
            }else {
                String clientId = args[1];
                String game = args[0];
                try {
                    XlrPlayerStatEntity xlrPlayerStatEntity;
                    if(game.equals("mw2")){
                        xlrPlayerStatEntity = xlrMw2StatsRepository.findByClientId(Integer.valueOf(clientId));
                    }else {
                        messageChannel.sendMessage("Supported games at this moment: `mw2`").complete();
                        return;
                    }
                    sendXlrPlayerStatMessage(game, clientId, xlrPlayerStatEntity, messageChannel);
                }catch (Exception e){
                    log.error("Failed to lookup client", e);
                    Message message = messageChannel.sendMessage("Can't process your message atm! try again later.").complete();
                }
            }
        } catch (Exception e) {
            log.error("Failed to send response for command !xlrstat", e);
        }
    }

    private void sendXlrPlayerStatMessage(String game, String clientId, XlrPlayerStatEntity xlrPlayerStatEntity, MessageChannel messageChannel) {
        if(xlrPlayerStatEntity == null){
            messageChannel.sendMessage("No stat found for client "+ clientId + " in xlr").complete();
            return;
        }

        messageChannel.sendMessage(DiscordUtil.getXlrStatResult(game, xlrPlayerStatEntity)).complete();
    }


}
