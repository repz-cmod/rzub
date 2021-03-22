package cmod.repz.application.service;

import cmod.repz.application.model.RepzBotProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomResponseService {
    private final RepzBotProperties repzBotProperties;

    public RandomResponseService(RepzBotProperties repzBotProperties) {
        this.repzBotProperties = repzBotProperties;
    }

    public String getRandomResponse(){
        List<String> responses = repzBotProperties.getResponses();
        return responses.get(new Random().nextInt(responses.size()));
    }
}
