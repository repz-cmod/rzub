package cmod.repz.application.service;

import cmod.repz.application.database.entity.repz.DiscordUserConfigEntity;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.repository.repz.DiscordUserConfigRepository;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.repz.GuildRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2ClientRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.event.DiscordReadyEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;

@Service
@Slf4j
public class Top10DiscordPrizeService implements ApplicationListener<DiscordReadyEvent> {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final XlrBo2ClientRepository xlrBo2ClientRepository;
    private final DiscordUserConfigRepository discordUserConfigRepository;
    private final DiscordUserRepository discordUserRepository;
    private final GuildRepository guildRepository;
    private final ConfigModel configModel;
    private Role mw2TopPlayerRole;
    private Role bo2TopPlayerRole;

    public Top10DiscordPrizeService(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, XlrBo2ClientRepository xlrBo2ClientRepository, DiscordUserConfigRepository discordUserConfigRepository, DiscordUserRepository discordUserRepository, GuildRepository guildRepository, ConfigModel configModel) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.xlrBo2ClientRepository = xlrBo2ClientRepository;
        this.discordUserConfigRepository = discordUserConfigRepository;
        this.discordUserRepository = discordUserRepository;
        this.guildRepository = guildRepository;
        this.configModel = configModel;
    }

    @Transactional
    public void handleTop10Enter(String game, DiscordUserEntity discordUserEntity){
        addRoleToUser(game.equals("mw2") ? mw2TopPlayerRole : bo2TopPlayerRole, discordUserEntity.getUserId());
        sendWelcomeMessage(discordUserEntity);
        if(!game.equals("mw2"))
            giveBo2Gift(discordUserEntity);
    }

    public void handleTop10Leave(String game, DiscordUserEntity discordUserEntity){
        removeRoleFromUser(game.equals("mw2") ? mw2TopPlayerRole : bo2TopPlayerRole, discordUserEntity.getUserId());
        if(!game.equals("mw2"))
            removeBo2Gift(discordUserEntity);
    }

    private void removeRoleFromUser(Role role, String userId){
        Assert.notNull(role, "Role can not be null");
        Assert.notNull(userId, "UserId can not be null");
        try {
            role.getGuild().removeRoleFromMember(userId, role).queue();
        }catch (Exception e){
            log.error("Failed to assign role to user", e);
        }
    }

    private void addRoleToUser(Role role, String userId){
        log.info("Adding role "+ role.getName() + " to user " + userId);
        Assert.notNull(role, "Role can not be null");
        Assert.notNull(userId, "UserId can not be null");
        try {
            role.getGuild().addRoleToMember(userId, role).queue();
        }catch (Exception e){
            log.error("Failed to assign role to user", e);
        }
    }

    private void giveBo2Gift(DiscordUserEntity discordUserEntity){
        xlrBo2ClientRepository.updateGreeting(Integer.parseInt(discordUserEntity.getB3BO2ClientId()), configModel.getMessages().get("greetingPrize"));
    }

    private void removeBo2Gift(DiscordUserEntity discordUserEntity){
        xlrBo2ClientRepository.updateGreeting(Integer.parseInt(discordUserEntity.getB3BO2ClientId()), "");
    }

    private void sendWelcomeMessage(DiscordUserEntity discordUserEntity){
        DiscordUserConfigEntity config = discordUserEntity.getConfig();
        if (config == null) {
            config = new DiscordUserConfigEntity();
            config.setTop10ChannelNotifSent(true);
            discordUserConfigRepository.save(config);
            discordUserEntity.setConfig(config);
            discordUserRepository.save(discordUserEntity);
        }else if(config.isTop10ChannelNotifSent()){
            return;
        }

        try {
            Guild guild = guildRepository.getGuild();
            String userAsMention = "<@"+discordUserEntity.getUserId()+">";
            String topPlayer = configModel.getMessages().get("topPlayer");
            String text = topPlayer.replace("$user", userAsMention);
            Message message = guild.getJDA().getTextChannelById(configModel.getDiscord().getChannels().get("topPlayers")).sendMessage(text).complete();

            config.setTop10ChannelNotifSent(true);
            discordUserConfigRepository.save(config);

            discordDelayedMessageRemoverService.scheduleRemove(message, 15 * 60);
        }catch (Exception e){
            log.error("Error while mentioning user", e);
        }
    }

    @Override
    public void onApplicationEvent(DiscordReadyEvent discordReadyEvent) {
        Guild guild = guildRepository.getGuild();
        mw2TopPlayerRole = guild.getRoleById(configModel.getDiscord().getRoles().get("topMw2Player"));
        bo2TopPlayerRole = guild.getRoleById(configModel.getDiscord().getRoles().get("topBo2Player"));
        log.info("Top10DiscordPrizeService is ready!");
    }
}
