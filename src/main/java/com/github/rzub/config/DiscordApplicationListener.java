package com.github.rzub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscordApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final DiscordListenerScanner discordListenerScanner;

    public DiscordApplicationListener(DiscordListenerScanner discordListenerScanner) {
        this.discordListenerScanner = discordListenerScanner;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Scanning discord listeners");
        discordListenerScanner.scan(contextRefreshedEvent.getApplicationContext());
    }
}
