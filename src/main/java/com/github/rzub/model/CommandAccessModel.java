package com.github.rzub.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandAccessModel {
    private List<AccessModel> commands;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccessModel {
        private String id;
        @JsonAlias("public")
        private boolean publicCommand = false;
        private List<String> roles = new ArrayList<>();
        private List<String> users = new ArrayList<>();
    }
}
