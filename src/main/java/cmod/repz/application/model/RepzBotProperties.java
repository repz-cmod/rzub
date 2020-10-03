package cmod.repz.application.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "repz.bot")
@Getter
@Setter
public class RepzBotProperties {
    private List<String> responses;
}
