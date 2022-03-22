package com.github.rzub.service.listener;

import com.github.rzub.config.DiscordStateHolder;
import com.github.rzub.database.repository.DiscordListenerRepository;
import com.github.rzub.database.repository.GuildRepository;
import com.github.rzub.model.event.DiscordMemberJoinEvent;
import com.github.rzub.model.event.DiscordReadyEvent;
import com.github.rzub.service.DiscordUserCacheService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
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
    private final DiscordUserCacheService discordUserCacheService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final GuildRepository guildRepository;

    public DiscordListener(DiscordListenerRepository discordListenerRepository, DiscordUserCacheService discordUserCacheService, ApplicationEventPublisher applicationEventPublisher, GuildRepository guildRepository) {
        this.discordListenerRepository = discordListenerRepository;
        this.discordUserCacheService = discordUserCacheService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.guildRepository = guildRepository;
    }

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        guildRepository.setGuild(event.getGuild());
        DiscordStateHolder.setReady(true);
        applicationEventPublisher.publishEvent(new DiscordReadyEvent(this, event.getGuild()));
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        log.info("User joined: "+ event.getUser().getName());
        discordUserCacheService.addToCache(event.getUser());
        applicationEventPublisher.publishEvent(new DiscordMemberJoinEvent(this, event));
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

        discordUserCacheService.addToCache(event.getAuthor());

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
