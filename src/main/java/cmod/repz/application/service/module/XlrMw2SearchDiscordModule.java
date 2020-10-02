package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.xlr.ClientEntity;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2ClientRepository;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/*
 * Searches in iw4admin for a player
 */
@DiscordListenerComponent(command = "xlrl")
@Slf4j
public class XlrMw2SearchDiscordModule implements DiscordCommandListener {
    private final XlrMw2ClientRepository xlrMw2ClientRepository;

    @Autowired
    public XlrMw2SearchDiscordModule(XlrMw2ClientRepository xlrMw2ClientRepository) {
        this.xlrMw2ClientRepository = xlrMw2ClientRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
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
                    }else {
                        messageChannel.sendMessage("Supported games at this moment: `mw2`").complete();
                        return;
                    }
                    sendResults(searchTerm, getClientsListAsString(clientEntities), messageChannel);
                }catch (Exception e){
                    log.error("Failed to send findClient request to iw4admin");
                    Message message = messageChannel.sendMessage("Can't process your message atm! try again later.").complete(true);
                }
            }
        } catch (RateLimitedException e) {
            log.error("Failed to send response for command !iwl", e);
        }
    }

    private String getClientsListAsString(List<ClientEntity> clientEntities){
        StringBuilder stringBuilder = new StringBuilder();
        clientEntities.forEach(clientEntity -> {
            stringBuilder.append(clientEntity.getName() + " ("+ clientEntity.getId() + ")\n");
        });
        return stringBuilder.toString();
    }

    private void sendResults(String searchTerm, String description, MessageChannel messageChannel){
        messageChannel.sendMessage(new EmbedBuilder()
                .setColor(Color.BLACK)
                .setTitle("XlrStats search results for *" + searchTerm + "*")
                .appendDescription(description)
                .build()).complete();
    }
}
