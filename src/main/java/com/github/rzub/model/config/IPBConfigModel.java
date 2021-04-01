package com.github.rzub.model.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPBConfigModel {
    @JsonAlias({"client-date-sensitive", "clientDateSensitive", "dateSensitive"})
    private boolean dateSensitive = false;
    @JsonAlias({"client-date-sensitive-hours", "clientDateSensitiveHours", "dateSensitiveHours"})
    private int dateSensitiveHours = 5;
}
