package cmod.repz.application.config;

import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.RepzBotProperties;
import cmod.repz.application.service.listener.DiscordListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

@Configuration
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(value = RepzBotProperties.class)
public class ApplicationConfig {
    private final DiscordListener discordListener;

    public ApplicationConfig(DiscordListener discordListener) {
        this.discordListener = discordListener;
    }

    @Bean
    public ConfigModel configModel() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File("./config.json"), ConfigModel.class);
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
        return builder.build();
    }
}
