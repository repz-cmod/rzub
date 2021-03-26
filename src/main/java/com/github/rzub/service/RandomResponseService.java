package com.github.rzub.service;

import com.github.rzub.model.RZUBBotProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomResponseService {
    private final RZUBBotProperties RZUBBotProperties;

    public RandomResponseService(RZUBBotProperties RZUBBotProperties) {
        this.RZUBBotProperties = RZUBBotProperties;
    }

    public String getRandomResponse(){
        List<String> responses = RZUBBotProperties.getResponses();
        return responses.get(new Random().nextInt(responses.size()));
    }
}
