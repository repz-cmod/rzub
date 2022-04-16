package com.github.rzub.service.discord;

import com.github.rzub.database.entity.TextConfigurationEntity;
import com.github.rzub.database.repository.TextConfigurationRepository;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DiscordController
public class TextConfigurationDiscordModule {

    private final TextConfigurationRepository textConfigurationRepository;

    public TextConfigurationDiscordModule(TextConfigurationRepository textConfigurationRepository) {
        this.textConfigurationRepository = textConfigurationRepository;
    }

    @DiscordCommand(name = "configuration-txt", description = "Setting string configuration setup", ephemeralDiffer = true)
    public void configuration(@DiscordParameter(name = "name") String name, @DiscordParameter(name="value") String value){
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        try {
            TextConfigurationEntity.ConfigurationNames.valueOf(name);
        } catch (Exception e){
            event.getHook().sendMessage("Invalid name. Try /configuration-txt-names").queue();
            return;
        }
        Optional<TextConfigurationEntity> optionalTextConfigurationEntity = textConfigurationRepository.findByName(name);
        TextConfigurationEntity textConfigurationEntity;
        if (optionalTextConfigurationEntity.isPresent()) {
            textConfigurationEntity = optionalTextConfigurationEntity.get();
        } else {
            textConfigurationEntity = new TextConfigurationEntity();
            textConfigurationEntity.setName(name);
        }
        textConfigurationEntity.setValue(value);
        textConfigurationRepository.save(textConfigurationEntity);
        event.getHook().sendMessage("Configuration updated").queue();
    }


    @DiscordCommand(name = "configuration-txt-names", description = "list configuration names", ephemeralDiffer = true)
    public void configurationNames(){
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        List<String> names = new ArrayList<>();
        for (TextConfigurationEntity.ConfigurationNames value : TextConfigurationEntity.ConfigurationNames.values()) {
            names.add(value.name());
        }
        event.getHook().sendMessage("Possible configuration names: " + String.join(", ", names)).queue();
    }

}
