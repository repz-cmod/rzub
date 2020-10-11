package cmod.repz.application.model.event;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiscordReadyEvent extends ApplicationEvent {
    private final Guild guild;

    public DiscordReadyEvent(Object source, Guild guild) {
        super(source);
        this.guild = guild;
    }
}
