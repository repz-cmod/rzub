package com.github.rzub.model.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IPBConfigModel {
    @JsonAlias({"client-date-sensitive", "clientDateSensitive"})
    private boolean dateSensitive = false;
}
