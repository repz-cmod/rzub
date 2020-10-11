package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.repz.GuildRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.event.XlrTopEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XlrTopPlayerDiscordRoleModule implements ApplicationListener<XlrTopEvent> {
    private final DiscordUserRepository discordUserRepository;
    private final GuildRepository guildRepository;
    private final ConfigModel configModel;
    private Role mw2TopPlayerRole;
    private Role bo2TopPlayerRole;
    private volatile XlrTopEvent previousEvent;
    private volatile boolean isReady = false;

    @Autowired
    public XlrTopPlayerDiscordRoleModule(ConfigModel configModel, DiscordUserRepository discordUserRepository, GuildRepository guildRepository) {
        this.discordUserRepository = discordUserRepository;
        this.guildRepository = guildRepository;
        this.configModel = configModel;
    }

    @Override
    public void onApplicationEvent(@NotNull XlrTopEvent xlrTopEvent) {
        if(!isReady){
            init();
            return;
        }

        log.info("Top mw2: " + xlrTopEvent.getMw2Stats());

        List<String> mw2ClientsToAddRole = getClientsToAddRole(xlrTopEvent.getMw2Stats(), previousEvent != null ? previousEvent.getMw2Stats() : new ArrayList<>());
        List<String> mw2ClientsToRemoveRole = getClientsToRemoveRole(xlrTopEvent.getMw2Stats(), previousEvent != null ? previousEvent.getMw2Stats() : new ArrayList<>());

        log.info("Users to add role to: " + mw2ClientsToAddRole);

        List<String> bo2ClientsToAddRole = getClientsToAddRole(xlrTopEvent.getBo2Stats(), previousEvent != null ? previousEvent.getBo2Stats() : new ArrayList<>());
        List<String> bo2ClientsToRemoveRole = getClientsToRemoveRole(xlrTopEvent.getBo2Stats(), previousEvent != null ? previousEvent.getBo2Stats() : new ArrayList<>());

        handle("mw2", mw2ClientsToAddRole, mw2ClientsToRemoveRole);
        handle("bo2", bo2ClientsToAddRole, bo2ClientsToRemoveRole);

        previousEvent = xlrTopEvent;
    }

    private void init() {
        Guild guild = guildRepository.getGuild();
        if(guild == null)
            return;

        mw2TopPlayerRole = guild.getRoleById(configModel.getDiscord().getRoles().get("topMw2Player"));
        bo2TopPlayerRole = guild.getRoleById(configModel.getDiscord().getRoles().get("topBo2Player"));

        isReady = true;
        log.info("XlrTopPlayerDiscordRoleModule is ready!");
    }

    private void handle(String game, List<String> toAdd, List<String> toRemove){
        List<DiscordUserEntity> adds;
        List<DiscordUserEntity> removes;
        if(game.equals("mw2")){
            adds = discordUserRepository.findAllByB3MW2ClientIdIn(toAdd);
            removes = discordUserRepository.findAllByB3MW2ClientIdIn(toRemove);
        }else{
            adds = discordUserRepository.findAllByB3BO2ClientIdIn(toAdd);
            removes = discordUserRepository.findAllByB3BO2ClientIdIn(toRemove);
        }

        adds.forEach(discordUserEntity -> {
            addRoleToUser(game.equals("mw2") ? mw2TopPlayerRole : bo2TopPlayerRole, discordUserEntity.getUserId());
        });

        removes.forEach(discordUserEntity -> {
            removeRoleFromUser(game.equals("mw2") ? mw2TopPlayerRole : bo2TopPlayerRole, discordUserEntity.getUserId());
        });
    }

    private List<String> getClientsToRemoveRole(List<XlrPlayerStatEntity> xlrPlayerStatEntities, List<XlrPlayerStatEntity> previousList){
        List<XlrPlayerStatEntity> toRemove = new ArrayList<>(previousList);
        toRemove.removeAll(xlrPlayerStatEntities);
        List<String> toRemoveIds = new ArrayList<>();
        toRemove.forEach(xlrPlayerStatEntity -> {
            toRemoveIds.add(String.valueOf(xlrPlayerStatEntity.getClient().getId()));
        });
        return toRemoveIds;
    }

    private List<String> getClientsToAddRole(List<XlrPlayerStatEntity> xlrPlayerStatEntities, List<XlrPlayerStatEntity> previousList){
        List<XlrPlayerStatEntity> toAdd = new ArrayList<>(xlrPlayerStatEntities);
        toAdd.removeAll(previousList);
        List<String> toAddIds = new ArrayList<>();
        toAdd.forEach(xlrPlayerStatEntity -> {
            toAddIds.add(String.valueOf(xlrPlayerStatEntity.getClient().getId()));
        });
        return toAddIds;
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
}
