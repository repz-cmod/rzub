package com.github.rzub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.model.config.IPBConfigModel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DynamicConfigurationProvider {
    private final SettingsModel settingsModel;
    private final ObjectMapper objectMapper;
    private final Map<Key, Class<?>> DYNAMIC_CONFIGURATION_MAPPING = new HashMap<>();

    @Autowired
    public DynamicConfigurationProvider(SettingsModel settingsModel, ObjectMapper objectMapper) {
        this.settingsModel = settingsModel;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init(){
        log.info("Initializing dynamic configuration service");
        DYNAMIC_CONFIGURATION_MAPPING.put(Key.IPB, IPBConfigModel.class);
    }

    @SneakyThrows
    @Cacheable(cacheNames = "dynamicConfiguration", key = "#key", unless="#result == null")
    public <E> E getDynamicConfiguration(Key key){
        JsonNode jsonNode = settingsModel.getCustom().get(key.getId());
        Class<?> aClass = DYNAMIC_CONFIGURATION_MAPPING.get(key);

        if(jsonNode == null){
            return (E) aClass.newInstance();
        }

        return (E) objectMapper.readValue(jsonNode.toString(), aClass);
    }

    @Getter
    public enum Key {
        IPB("ipb");
        private final String id;

        Key(String id) {
            this.id = id;
        }
    }

}
