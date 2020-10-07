package cmod.repz.application.service.listener;

import cmod.repz.application.database.repository.repz.DiscordListenerRepository;
import cmod.repz.application.model.event.DiscordMemberJoinEVent;
import cmod.repz.application.service.DiscordUserCache;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Component
@Slf4j
public class DiscordListener extends ListenerAdapter {
    private final DiscordListenerRepository discordListenerRepository;
    private final DiscordUserCache discordUserCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DiscordListener(DiscordListenerRepository discordListenerRepository, DiscordUserCache discordUserCache, ApplicationEventPublisher applicationEventPublisher) {
        this.discordListenerRepository = discordListenerRepository;
        this.discordUserCache = discordUserCache;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        log.info("User joined: "+ event.getUser().getName());
        discordUserCache.addToCache(event.getUser());
        applicationEventPublisher.publishEvent(new DiscordMemberJoinEVent(this, event));
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

        discordUserCache.addToCache(event.getAuthor());

        if(messageContent.startsWith("!")){
            String substring = messageContent.substring(1);
            String[] commandAndArgs = substring.split(" ");
            Object listenerOfCommand = discordListenerRepository.getListenerOfCommand(commandAndArgs[0]);
            if(listenerOfCommand != null){
                try {
                    String[] args;
                    if(commandAndArgs.length > 1){
                        args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length);
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
