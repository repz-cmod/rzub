package com.github.rzub.service.discord;

import com.github.rzub.database.entity.CookieEntity;
import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.database.repository.IW4MAdminUserRepository;
import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.service.api.IW4MAdminApiService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.hibernate.Hibernate;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@DiscordController
@Slf4j
public class IW4MAdminLoginDiscordModule {
    private final IW4MAdminApiService iw4MAdminApiService;
    private final DiscordUserRepository discordUserRepository;
    private final IW4MAdminUserRepository iw4MAdminUserRepository;
    private final CookieRepository cookieRepository;

    public IW4MAdminLoginDiscordModule(IW4MAdminApiService iw4MAdminApiService, DiscordUserRepository discordUserRepository, IW4MAdminUserRepository iw4MAdminUserRepository, CookieRepository cookieRepository) {
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.discordUserRepository = discordUserRepository;
        this.iw4MAdminUserRepository = iw4MAdminUserRepository;
        this.cookieRepository = cookieRepository;
    }

    @DiscordCommand(name = "botiwlogin", description = "Log the bot into iw4madmin")
    public void onBotLogin(@DiscordParameter(name="client-id") String clientId, @DiscordParameter(name="password") String password) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        try {
            iw4MAdminApiService.logIn(clientId, password);
            event.getHook().sendMessage("Successfully logged in to iw4madmin :)").queue();
        } catch (Exception e) {
            log.error("Failed to login", e);
            event.getHook().sendMessage("Error while logging in to iw4madmin :(").queue();
        }
    }


    @DiscordCommand(name = "iwlogin", description = "Log the user into iw4madmin")
    public void onUserLogin(@DiscordParameter(name="client-id") String clientId, @DiscordParameter(name="password") String password) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        Member eventMember = event.getMember();
        try {
            CookieEntity cookieEntity = iw4MAdminApiService.logIn(clientId, password);

            // Get user if is already registered or register user without token
            DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(eventMember).getUser().getId());
            if(discordUserEntity == null) {
                discordUserEntity = register(eventMember);
            }

            // Get iw4MAdminUserEntity of the logged in client id if already exists
            List<IW4MAdminUserEntity> iw4MAdminUserEntities = discordUserEntity.getIw4MAdminUserEntities();
            Hibernate.initialize(discordUserEntity.getIw4MAdminUserEntities());
            IW4MAdminUserEntity iw4MAdminUserEntity = getIW4MAdminUserEntityOfClientId(iw4MAdminUserEntities, clientId);

            // Get iw4MAdminUserEntity of the logged in client id if doesnt already exist
            if (iw4MAdminUserEntity == null){
                for (Iw4madminApiModel.Stat clientStat : iw4MAdminApiService.getClientStats(clientId)) {
                    for (Iw4madminApiModel.BasicClient client : iw4MAdminApiService.findClient(clientStat.getName()).getClients()) {
                        if (Integer.valueOf(clientId).equals(client.getClientId())){
                            iw4MAdminUserEntity = getIW4MAdminUserEntity(client, clientStat, discordUserEntity);
                            break;
                        }
                    }
                    if (iw4MAdminUserEntity != null){
                        break;
                    }
                }
            }

            // We failed to get client?
            if (iw4MAdminUserEntity == null){
                event.getHook().sendMessage("Strange error happened. Error ID: #1").queue();
                return;
            }

            // Set iw4MAdminUserEntity of the logged in client on the cookie
            cookieEntity.setIw4MAdminUser(iw4MAdminUserEntity);
            cookieRepository.save(cookieEntity);

            event.getHook().sendMessage("Successfully logged you in to iw4madmin :)").queue();
        } catch (Exception e) {
            log.error("Failed to login", e);
            event.getHook().sendMessage("Error while logging in to iw4madmin :(").queue();
        }
    }

    private IW4MAdminUserEntity getIW4MAdminUserEntityOfClientId(List<IW4MAdminUserEntity> iw4MAdminUserEntities, String clientId) {
        if (iw4MAdminUserEntities == null)
            return null;
        for (IW4MAdminUserEntity iw4MAdminUserEntity : iw4MAdminUserEntities) {
            if (Long.parseLong(clientId) == iw4MAdminUserEntity.getClientId()) {
                return iw4MAdminUserEntity;
            }
        }
        return null;
    }

    private IW4MAdminUserEntity getIW4MAdminUserEntity(Iw4madminApiModel.BasicClient client, Iw4madminApiModel.Stat clientStat, DiscordUserEntity discordUserEntity) {
        IW4MAdminUserEntity iw4MAdminUserEntity = IW4MAdminUserEntity.builder()
                .game(clientStat.getServerGame())
                .name(client.getName())
                .clientId((long) client.getClientId())
                .creationDate(new Date())
                .done(true)
                .guid(client.getXuid())
                .discordUser(discordUserEntity)
                .build();
        iw4MAdminUserRepository.save(iw4MAdminUserEntity);
        return iw4MAdminUserEntity;
    }

    private DiscordUserEntity register(Member member) {
        DiscordUserEntity discordUserEntity = new DiscordUserEntity();
        discordUserEntity.setUserId(member.getUser().getId());
        discordUserEntity.setCreationDate(new Date());
        discordUserEntity.setNickname(member.getNickname());
        discordUserEntity.setUsername(member.getUser().getName());
        discordUserEntity.setMessageSent(true);
        discordUserEntity.setToken(UUID.randomUUID().toString());
        discordUserRepository.save(discordUserEntity);
        Hibernate.initialize(discordUserEntity);
        Hibernate.initialize(discordUserEntity.getIw4MAdminUserEntities());
        return discordUserEntity;
    }

}
