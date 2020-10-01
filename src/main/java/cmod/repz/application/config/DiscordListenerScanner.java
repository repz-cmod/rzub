package cmod.repz.application.config;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.repository.DiscordListenerRepository;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.service.listener.DiscordMessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DiscordListenerScanner {
    private final DiscordListenerRepository discordListenerRepository;

    public DiscordListenerScanner(DiscordListenerRepository discordListenerRepository) {
        this.discordListenerRepository = discordListenerRepository;
    }

    public void scan(ApplicationContext applicationContext){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(DiscordListenerComponent.class);
        beansWithAnnotation.values().forEach(bean -> {
            if(bean instanceof DiscordCommandListener){
                DiscordListenerComponent discordListenerComponent = bean.getClass().getAnnotation(DiscordListenerComponent.class);
                discordListenerRepository.addCommandListener(discordListenerComponent.command(), (DiscordCommandListener) discordListenerComponent);
            }else if(bean instanceof DiscordMessageListener){
                discordListenerRepository.addMessageListener((DiscordMessageListener) bean);
            }
        });
    }
}
