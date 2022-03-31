package com.github.rzub.service.discord;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.model.SettingsModel;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.Objects;

/*
 * Registers a player
 */
@Slf4j
@DiscordController
public class UserRegistrationDiscordModule  {
    private final DiscordUserRepository discordUserRepository;
    private final SettingsModel settingsModel;

    @Autowired
    public UserRegistrationDiscordModule(DiscordUserRepository discordUserRepository, SettingsModel settingsModel) {
        this.discordUserRepository = discordUserRepository;
        this.settingsModel = settingsModel;
    }

    @DiscordCommand(name = "register", description = "registers your discord account to our servers")
    public void onCommand() {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        Member eventMember = event.getMember();
        try {
            DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(eventMember).getUser().getId());
            if(discordUserEntity == null){
                discordUserEntity = new DiscordUserEntity();
                discordUserEntity.setUserId(eventMember.getUser().getId());
                discordUserEntity.setCreationDate(new Date());
                discordUserEntity.setNickname(eventMember.getNickname());
                discordUserEntity.setUsername(eventMember.getUser().getName());
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
                sendMessage(event, discordUserEntity);
            }else if(!discordUserEntity.isMessageSent()){
                sendMessage(event, discordUserEntity);
            }else {
                event.getHook().sendMessage("Your token has been sent to you before").queue();
            }

        }catch (Exception e){
            log.error("Failed to handle user registration", e);
        }
    }

    private void sendMessage(SlashCommandEvent event, DiscordUserEntity discordUserEntity) {
        event.getHook().sendMessage("Sending you a private message ...").queue();
        String message = new String(settingsModel.getMessages().get("registration")).replaceAll("\\$token", discordUserEntity.getToken());
        event.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(message)).queue(message1 -> {
            discordUserEntity.setMessageSent(true);
            discordUserRepository.save(discordUserEntity);
        });
    }

}
