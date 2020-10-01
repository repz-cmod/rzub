package cmod.repz.application.service.listener;

import cmod.repz.application.database.repository.DiscordListenerRepository;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Arrays;

@Component
public class DiscordListener extends ListenerAdapter {
    private final DiscordListenerRepository discordListenerRepository;

    public DiscordListener(DiscordListenerRepository discordListenerRepository) {
        this.discordListenerRepository = discordListenerRepository;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage()) {
            return;
        }
        String messageContent = event.getMessage().getContentRaw();

        if(messageContent.length() < 3){
            return;
        }

        if(messageContent.startsWith("!")){
            String substring = messageContent.substring(1);
            String[] commandAndArgs = substring.split(" ");
            DiscordCommandListener listenerOfCommand = discordListenerRepository.getListenerOfCommand(commandAndArgs[0]);
            if(listenerOfCommand != null){
                listenerOfCommand.onCommand(event, Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length-1));
            }
        }else {
            discordListenerRepository.getMessageListeners().forEach(discordMessageListener -> {
                discordMessageListener.onMessage(event, messageContent);
            });
        }

    }
}
