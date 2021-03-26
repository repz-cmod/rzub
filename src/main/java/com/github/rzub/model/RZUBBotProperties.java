package com.github.rzub.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "rzub.bot")
@Getter
@Setter
public class RZUBBotProperties {
    private List<String> responses;
}
