package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.xlr.ClientEntity;
import cmod.repz.application.database.repository.xlr.bf3.bo2.XlrBf3ClientRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2ClientRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2ClientRepository;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/*
 * Searches in iw4admin for a player
 */
@DiscordListenerComponent(command = "xlrl", description = "looks up for player in xlr")
@Slf4j
public class XlrLookupDiscordModule implements DiscordCommandListener {
    private final XlrMw2ClientRepository xlrMw2ClientRepository;
    private final XlrBo2ClientRepository xlrBo2ClientRepository;
    private final XlrBf3ClientRepository xlrBf3ClientRepository;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    @Autowired
    public XlrLookupDiscordModule(XlrMw2ClientRepository xlrMw2ClientRepository, XlrBo2ClientRepository xlrBo2ClientRepository, XlrBf3ClientRepository xlrBf3ClientRepository, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.xlrMw2ClientRepository = xlrMw2ClientRepository;
        this.xlrBo2ClientRepository = xlrBo2ClientRepository;
        this.xlrBf3ClientRepository = xlrBf3ClientRepository;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 120);
        MessageChannel messageChannel = event.getMessage().getChannel();
        if(args.length < 2){
            messageChannel.sendMessage("Please provide name to search xlr api. Sample: `!xlrl <game> <player name query>`.").complete();
        }else {
            String game = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
            String searchTerm;
            if(args.length > 1){
                searchTerm = String.join(" ", args);
            }else {
                searchTerm = args[0];
            }
            try {
                List<ClientEntity> clientEntities;
                if(game.toLowerCase().equals("mw2")){
                    clientEntities = xlrMw2ClientRepository.findAllByNameLike(searchTerm);
                }else if(game.toLowerCase().equals("bo2")) {
                    clientEntities = xlrBo2ClientRepository.findAllByNameLike(searchTerm);
                }else if(game.toLowerCase().equals("bf3")){
                    clientEntities = xlrBf3ClientRepository.findAllByNameLike(searchTerm);
                }else {
                    messageChannel.sendMessage("Supported games at this moment: `mw2`, `bo2`, `bf3`").complete();
                    return;
                }
                sendResults(searchTerm, getClientsListAsString(clientEntities), messageChannel);
            }catch (Exception e){
                log.error("Failed to send findClient request to iw4admin");
                messageChannel.sendMessage("Can't process your message atm! try again later.").complete();
            }
        }

    }

    private String getClientsListAsString(List<ClientEntity> clientEntities){
        StringBuilder stringBuilder = new StringBuilder();
        clientEntities.forEach(clientEntity -> {
            stringBuilder.append(clientEntity.getName()).append(" (").append(clientEntity.getId()).append(")\n");
        });
        return stringBuilder.toString();
    }

    private void sendResults(String searchTerm, String description, MessageChannel messageChannel){
        discordDelayedMessageRemoverService.scheduleRemove(messageChannel.sendMessage(new EmbedBuilder()
                .setColor(Color.BLACK)
                .setTitle("XlrStats search results for *" + searchTerm + "*")
                .appendDescription(description)
                .build()).complete(), 120);
    }
}
