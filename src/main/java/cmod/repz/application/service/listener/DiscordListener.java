package cmod.repz.application.service.listener;

import cmod.repz.application.database.repository.DiscordListenerRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Component
@Slf4j
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
            Object listenerOfCommand = discordListenerRepository.getListenerOfCommand(commandAndArgs[0]);
            if(listenerOfCommand != null){
                try {
                    String[] args;
                    if(commandAndArgs.length > 1){
                        args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length - 1);
                    }else {
                        args = new String[]{};
                    }
                    listenerOfCommand.getClass().getMethod("onCommand", GuildMessageReceivedEvent.class, String[].class).invoke(listenerOfCommand, event, args);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.error("Failed to invoke method `onCommand()`", e);
                }
            }
        }else {
            discordListenerRepository.getMessageListeners().forEach(discordMessageListener -> {
                try {
                    discordMessageListener.getClass().getMethod("onMessage", GuildMessageReceivedEvent.class).invoke(discordMessageListener, event);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.error("Failed to invoke method `onMessage()`", e);
                }
            });
        }

    }
}
