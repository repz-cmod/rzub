package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;

/*
 * Registers a player
 */
@DiscordListenerComponent(command = "register", description = "registers your discord account to our servers")
@Slf4j
public class UserRegistrationDiscordModule implements DiscordCommandListener {
    private final DiscordUserRepository discordUserRepository;
    private final SettingsModel settingsModel;

    @Autowired
    public UserRegistrationDiscordModule(DiscordUserRepository discordUserRepository, SettingsModel settingsModel) {
        this.discordUserRepository = discordUserRepository;
        this.settingsModel = settingsModel;
    }

    @Override
    @Transactional
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        try {
            DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId());
            if(discordUserEntity == null){
                discordUserEntity = new DiscordUserEntity();
                discordUserEntity.setUserId(event.getMember().getUser().getId());
                discordUserEntity.setCreationDate(new Date());
                discordUserEntity.setNickname(event.getMember().getNickname());
                discordUserEntity.setUsername(event.getMember().getUser().getName());
                boolean saved = false;
                String token = "";
                while (!saved){
                    try {
                        token = RandomStringUtils.random(6, true, true);
                        discordUserEntity.setToken(token);
                        discordUserRepository.save(discordUserEntity);
                        saved = true;
                    }catch (DataIntegrityViolationException e){}
                }

                sendMessage(event, token);
                messageSent(discordUserEntity);
            }else {
                if(!discordUserEntity.isMessageSent()){
                    sendMessage(event, discordUserEntity.getToken());
                    messageSent(discordUserEntity);
                }
            }
            event.getMessage().delete().complete();
        }catch (Exception e){
            log.error("Failed to handle user registration", e);
        }
    }

    private void sendMessage(GuildMessageReceivedEvent event, String token) {
        String message = new String(settingsModel.getMessages().get("registration")).replaceAll("\\$token", token);
        event.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(message)).queue();
    }

    private void messageSent(DiscordUserEntity discordUserEntity){
        discordUserEntity.setMessageSent(true);
        discordUserRepository.save(discordUserEntity);
    }
}
