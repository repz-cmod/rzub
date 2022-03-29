package com.github.rzub.config;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.repository.DiscordListenerRepository;
import com.github.rzub.service.listener.DiscordMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class DiscordListenerScanner {
    private final DiscordListenerRepository discordListenerRepository;

    public DiscordListenerScanner(DiscordListenerRepository discordListenerRepository) {
        this.discordListenerRepository = discordListenerRepository;
    }

    public void scan(ApplicationContext applicationContext){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(DiscordListenerComponent.class);
        if(beansWithAnnotation != null){
            beansWithAnnotation.values().forEach(bean -> {
                if(AopUtils.isAopProxy(bean)){
                    Class<?> aClass = AopUtils.getTargetClass(bean);
                    DiscordListenerComponent discordListenerComponent = aClass.getAnnotation(DiscordListenerComponent.class);
                    if(discordListenerComponent == null)
                        return;
                    if(bean instanceof DiscordMessageListener){
                        discordListenerRepository.addMessageListener(bean);
                    }
                }

            });
        }

    }
}
