package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.xlr.bf3.bo2.XlrBf3StatsRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Objects;

/*
 * Gets registered player stats from xlrstats
 */
@DiscordListenerComponent(command = "xlrstats", description = "returns player xlrstats")
@Slf4j
public class XlrStatsDiscordModule implements DiscordCommandListener {
    private final XlrMw2StatsRepository xlrMw2StatsRepository;
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final XlrBf3StatsRepository xlrBf3StatsRepository;
    private final DiscordUserRepository discordUserRepository;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final ConfigModel configModel;


    public XlrStatsDiscordModule(XlrMw2StatsRepository xlrMw2StatsRepository, XlrBo2StatsRepository xlrBo2StatsRepository, XlrBf3StatsRepository xlrBf3StatsRepository, DiscordUserRepository discordUserRepository, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, ConfigModel configModel) {
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.xlrBf3StatsRepository = xlrBf3StatsRepository;
        this.discordUserRepository = discordUserRepository;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.configModel = configModel;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 120);
            MessageChannel messageChannel = event.getMessage().getChannel();
            if(args.length < 1){
                discordDelayedMessageRemoverService.scheduleRemove(messageChannel.sendMessage("Please provide clientId and game. Like: \n`!xlrstats <game> <clientId>` for none-registered users. \n`!xlrstats <game>` for registered users. \nSupported values for game: `mw2`, `bo2`.").complete(), 60);
            }else {
                String game = args[0].toLowerCase();
                String clientId = null;
                if(args.length == 2){
                    clientId = args[1];
                    if(!isNumeric(clientId) && clientId.startsWith("@")){
                        if (event.getMessage().getMentionedMembers().size() > 0) {
                            try {
                                clientId = getClientIdByDiscordUser(Objects.requireNonNull(event.getMessage().getMentionedMembers().get(0)).getUser().getId(), game);
                            }catch (Exception e){
                                discordDelayedMessageRemoverService.scheduleRemove(messageChannel.sendMessage("Cant find the mentioned user in DB!").complete(), 60);
                                return;
                            }
                        }
                    }
                }else {
                    try {
                        clientId = getClientIdByDiscordUser(Objects.requireNonNull(event.getMember()).getUser().getId(), game);
                    }catch (Exception e){
                        discordDelayedMessageRemoverService.scheduleRemove(messageChannel.sendMessage("You should first try to `!register`").complete(), 60);
                        return;
                    }
                }

                try {
                    XlrPlayerStatEntity xlrPlayerStatEntity;
                    if(game.equals("mw2") || game.equals("iw4x")){
                        game = "IW4X";
                        xlrPlayerStatEntity = xlrMw2StatsRepository.findByClientId(Integer.parseInt(clientId));
                    } else if(game.equals("bo2") || game.equals("t6")){
                        game = "Plutonium T6";
                        xlrPlayerStatEntity = xlrBo2StatsRepository.findByClientId(Integer.parseInt(clientId));
                    } else if(game.equals("bf3") || game.equals("zlo")){
                        game = "BattleField 3";
                        xlrPlayerStatEntity = xlrBf3StatsRepository.findByClientId(Integer.parseInt(clientId));
                    } else {
                        messageChannel.sendMessage("Supported games at this moment: `mw2`, `t6`, and `bf3` (you cant register for `bf3`)").complete();
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
        Message message;
        if(xlrPlayerStatEntity == null){
            message = messageChannel.sendMessage("No stat found for client "+ clientId + " in xlr").complete();
        }else {
            String prefix = null;
            if(game.equals("mw2")){
                prefix = configModel.getXlrMw2Prefix();
            }else {
                prefix = configModel.getXlrBo2Prefix();
            }
            message = messageChannel.sendMessage(DiscordUtil.getXlrStatResult(game, xlrPlayerStatEntity, prefix + xlrPlayerStatEntity.getId())).complete();
        }
        discordDelayedMessageRemoverService.scheduleRemove(message, 120);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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
        }else if(game.equals("bo2")) {
            String clientId = discordUserEntity.getB3BO2ClientId();
            if(clientId == null){
                throw new Exception("User not found");
            }
            return clientId;
        }
        throw new Exception("User not found");
    }

}
