package cmod.repz.application.model.event;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiscordPlayerRegisterEvent extends ApplicationEvent {
    private final DiscordUserEntity discordUserEntity;
    private final String game;

    public DiscordPlayerRegisterEvent(Object source, DiscordUserEntity discordUserEntity, String game) {
        super(source);
        this.discordUserEntity = discordUserEntity;
        this.game = game;
    }
}
