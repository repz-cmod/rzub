package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

/*
 * Gets registered player stats from iw4admin
 */
@DiscordListenerComponent(command = "xlrstats", description = "returns player xlrstats")
@Slf4j
public class XlrStatsDiscordModule implements DiscordCommandListener {
    private final XlrMw2StatsRepository xlrMw2StatsRepository;
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final DiscordUserRepository discordUserRepository;
    private final ConfigModel configModel;

    public XlrStatsDiscordModule(XlrMw2StatsRepository xlrMw2StatsRepository, XlrBo2StatsRepository xlrBo2StatsRepository, DiscordUserRepository discordUserRepository, ConfigModel configModel) {
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.discordUserRepository = discordUserRepository;
        this.configModel = configModel;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            MessageChannel messageChannel = event.getMessage().getChannel();
            if(args.length < 1){
                messageChannel.sendMessage("Please provide clientId and game. Like: `!xlrstats <game> <clientId>` for none-registered users and `!xlrstats <game>` for registered users").complete();
            }else {
                String game = args[0].toLowerCase();
                String clientId = null;
                if(args.length == 2){
                    clientId = args[1];
                }else {
                    try {
                        clientId = getClientIdByDiscordUser(Objects.requireNonNull(event.getMember()).getUser().getId(), game);
                    }catch (Exception e){
                        messageChannel.sendMessage("Either your user is not registered or you haven't registered in this game").complete();
                        return;
                    }
                }

                try {
                    XlrPlayerStatEntity xlrPlayerStatEntity;
                    if(game.equals("mw2")){
                        xlrPlayerStatEntity = xlrMw2StatsRepository.findByClientId(Integer.parseInt(clientId));
                    } else if(game.equals("bo2")){
                        xlrPlayerStatEntity = xlrBo2StatsRepository.findByClientId(Integer.parseInt(clientId));
                    } else {
                        messageChannel.sendMessage("Supported games at this moment: `mw2`").complete();
                        return;
                    }
                    sendXlrPlayerStatMessage(game, clientId, xlrPlayerStatEntity, messageChannel);
                }catch (Exception e){
                    log.error("Failed to lookup client", e);
                    messageChannel.sendMessage("Can't process your message atm! try again later.").complete();
                }
            }
        } catch (Exception e) {
            log.error("Failed to send response for command !xlrstats", e);
        }
    }

    private void sendXlrPlayerStatMessage(String game, String clientId, XlrPlayerStatEntity xlrPlayerStatEntity, MessageChannel messageChannel) {
        if(xlrPlayerStatEntity == null){
            messageChannel.sendMessage("No stat found for client "+ clientId + " in xlr").complete();
            return;
        }

        messageChannel.sendMessage(DiscordUtil.getXlrStatResult(game, xlrPlayerStatEntity, configModel.getXlrMw2Prefix() + xlrPlayerStatEntity.getId())).complete();
    }

    private String getClientIdByDiscordUser(String userId, String game) throws Exception {
        DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(userId);
        if(discordUserEntity == null) {
            throw new Exception("User not found");
        }
        if(game.equals("mw2")){
            String b3MW2ClientId = discordUserEntity.getB3MW2ClientId();
            if(b3MW2ClientId == null){
                throw new Exception("User not found");
            }
            return b3MW2ClientId;
        }
        //todo: bo2
        throw new Exception("User not found");
    }

}
