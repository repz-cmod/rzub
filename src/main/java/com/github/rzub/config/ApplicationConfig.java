package com.github.rzub.config;

import com.github.rzub.model.CommandAccessModel;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.model.RZUBBotProperties;
import com.github.rzub.service.listener.DiscordListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class ApplicationConfig {
    private final DiscordListener discordListener;

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
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(configFileAddress), SettingsModel.class);
    }

    @Bean
    @DependsOn("settingsModel")
    public JDA discord(SettingsModel settingsModel) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(settingsModel.getDiscord().getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching("IW4MAdmin Servers"));
        builder.addEventListeners(discordListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }
}
