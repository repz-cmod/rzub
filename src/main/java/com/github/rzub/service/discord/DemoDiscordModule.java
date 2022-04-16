package com.github.rzub.service.discord;


import com.github.rzub.database.entity.TextConfigurationEntity;
import com.github.rzub.database.repository.TextConfigurationRepository;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Optional;

@DiscordController
public class DemoDiscordModule {
    private final TextConfigurationRepository textConfigurationRepository;

    public DemoDiscordModule(TextConfigurationRepository textConfigurationRepository) {
        this.textConfigurationRepository = textConfigurationRepository;
    }

    @DiscordCommand(name = "demo", description = "Describes how to get demo of iw4x game")
    public void explainDemo(){
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        Optional<TextConfigurationEntity> optionalTextConfigurationEntity = textConfigurationRepository.findByName(TextConfigurationEntity.ConfigurationNames.DEMO_MESSAGE.toString());
        if (optionalTextConfigurationEntity.isPresent())
            event.getHook().sendMessage(optionalTextConfigurationEntity.get().getValue()).queue();
        else
            event.getHook().sendMessage("Value is not set for the demo command").queue();
    }

}
