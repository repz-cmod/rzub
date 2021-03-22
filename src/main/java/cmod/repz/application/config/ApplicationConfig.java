package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.RepzBotProperties;
import cmod.repz.application.service.listener.DiscordListener;
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
@EnableConfigurationProperties(value = RepzBotProperties.class)
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
    public ConfigModel configModel(@Value("${repz.conf.file}") String configFileAddress) throws IOException {
        System.out.println("---> " + configFileAddress);
        ObjectMapper objectMapper = new ObjectMapper();
        ConfigModel configModel = objectMapper.readValue(new File(configFileAddress), ConfigModel.class);
        System.out.println("===>" + configModel);
        return configModel;
    }

    @Bean
    @DependsOn("configModel")
    public JDA discord(ConfigModel configModel) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(configModel.getDiscord().getToken());
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching("Repz Servers"));
        builder.addEventListeners(discordListener);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        return builder.build();
    }
}
