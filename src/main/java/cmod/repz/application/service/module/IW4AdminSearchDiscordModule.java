package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.service.api.IW4AdminApi;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

@DiscordListenerComponent(command = "iwl")
@Slf4j
public class IW4AdminSearchDiscordModule implements DiscordCommandListener {
    private final IW4AdminApi iw4AdminApi;

    @Autowired
    public IW4AdminSearchDiscordModule(IW4AdminApi iw4AdminApi) {
        this.iw4AdminApi = iw4AdminApi;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
            try {
                MessageChannel messageChannel = event.getMessage().getChannel();
                if(args.length == 0){
                    Message message = messageChannel.sendMessage("Please provide name to search iw4admin api").complete(true);
                }else {
                    String searchTerm;
                    if(args.length > 1){
                        searchTerm = String.join(" ", args);
                    }else {
                        searchTerm = args[0];
                    }
                    try {
                        Iw4adminApiModel.FindApiResult findApiResult = iw4AdminApi.findClient(searchTerm);
                        StringBuilder stringBuilder = new StringBuilder();
                        findApiResult.getClients().forEach(basicClient -> {
                            stringBuilder.append(basicClient.getName() + " ("+ basicClient.getClientId() + ")\n");
                        });
                        messageChannel.sendMessage(new EmbedBuilder()
                                .setColor(Color.BLACK)
                                .setTitle("IW4Admin search results for *" + searchTerm + "*")
                                .appendDescription(stringBuilder.toString())
                                .build()).complete();
                    }catch (Exception e){
                        log.error("Failed to send findClient request to iw4admin");
                        Message message = messageChannel.sendMessage("Can't process your message atm! try again later.").complete(true);
                    }

                }

            } catch (RateLimitedException e) {
                log.error("Failed to send response for command !iwl", e);
            }
    }
}
