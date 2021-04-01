package com.github.rzub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rzub.model.CommandAccessModel;
import com.github.rzub.model.RZUBBotProperties;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.listener.DiscordListener;
import com.github.rzub.util.DiscordUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(value = RZUBBotProperties.class)
@Slf4j
public class ApplicationConfig implements ApplicationContextAware {
    private final DiscordListener discordListener;
    private ApplicationContext applicationContext;

    public ApplicationConfig(DiscordListener discordListener) {
        this.discordListener = discordListener;
    }

    @Bean("messageRemoveTaskScheduler")
    public synchronized TaskScheduler messageRemoveTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(3);
        taskScheduler.setAwaitTerminationSeconds(50);
        taskScheduler.setBeanName("notificationAsyncTask");
        taskScheduler.setDaemon(false);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        taskScheduler.afterPropertiesSet();
        return taskScheduler;
    }

    @Bean
    public CommandAccessModel commandAccessModel(@Value("${rzub.conf.access}") String configFileAddress) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(configFileAddress), CommandAccessModel.class);
    }

    @Bean
    public SettingsModel settingsModel(@Value("${rzub.conf.settings}") String configFileAddress) throws IOException {
        File file = new File(configFileAddress);
        if(!file.exists()){
            log.error("settings.json file was not found");
            System.exit(0);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SettingsModel settingsModel = objectMapper.readValue(file, SettingsModel.class);
        DiscordUtil.setup(settingsModel);
        return settingsModel;
    }

    @Bean
    @DependsOn("settingsModel")
    public JDA discord(SettingsModel settingsModel) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(settingsModel.getDiscord().getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.addEventListeners(discordListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
