package com.github.rzub.service.discord;

import com.github.rzub.database.entity.WhitelistEntity;
import com.github.rzub.database.repository.WhitelistRepository;
import com.github.rzub.model.SettingsModel;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Date;

@DiscordController
public class WhitelistDiscordModule {
    private final WhitelistRepository whitelistRepository;

    public WhitelistDiscordModule(WhitelistRepository whitelistRepository, SettingsModel settingsModel) {
        this.whitelistRepository = whitelistRepository;
    }

    @DiscordCommand(name = "whitelist-add", description = "Add client to `ipb` and `ipb2` whitelist")
    public void onAdd(@DiscordParameter(name = "client-id") Integer clientId) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();

        try {
            whitelistRepository.save(WhitelistEntity.builder()
                    .clientId(clientId)
                    .username(event.getMember().getEffectiveName())
                    .creationDate(new Date())
                    .build());
            event.getHook().sendMessage("Added client to whitelist.").queue();
        }catch (Exception e){
            event.getHook().sendMessage("Failed: `"+e.getMessage()+"`").queue();
        }
    }

    @DiscordCommand(name = "whitelist-test", description = "Test client availability in `ipb` and `ipb2` whitelist")
    public void onTest(@DiscordParameter(name = "client-id") Integer clientId) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        event.getHook().sendMessage(whitelistRepository.existsByClientId(clientId) ? "+" : "-" + " `" + clientId + "`").queue();
    }

    @DiscordCommand(name = "whitelist-rm", description = "Remove client from `ipb` and `ipb2` whitelist")
    public void onRemove(@DiscordParameter(name = "client-id") Integer clientId) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        whitelistRepository.deleteByClientId(clientId);
        event.getHook().sendMessage("Client is removed").queue();
    }

}
