package com.github.rzub.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscordRegisterDto {
    private String playerName;
    private String xuid;
    private String clientId;
    private String token;
    private String game;
}
