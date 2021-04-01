package com.github.rzub.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CommandAliasModel {
    private Map<String, Set<String>> aliases = new HashMap<>();
}
