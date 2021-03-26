package com.github.rzub.service;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.model.ConfigModel;
import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.model.dto.AbstractResultDto;
import com.github.rzub.model.dto.DiscordRegisterDto;
import com.github.rzub.model.dto.FailedResultDto;
import com.github.rzub.model.dto.SuccessResultDto;
import com.github.rzub.model.event.DiscordPlayerRegisterEvent;
import com.github.rzub.service.api.IW4MAdminApiService;
import com.github.rzub.util.GameUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class RegistrationFinalizeService {
    private final JDA jda;
    private final DiscordUserRepository discordUserRepository;
    private final ConfigModel configModel;
    private final IW4MAdminApiService iw4MAdminApiService;
    private final DiscordUserCacheService discordUserCacheService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public RegistrationFinalizeService(JDA jda, DiscordUserRepository discordUserRepository, ConfigModel configModel, IW4MAdminApiService iw4MAdminApiService, DiscordUserCacheService discordUserCacheService, ApplicationEventPublisher applicationEventPublisher) {
        this.jda = jda;
        this.discordUserRepository = discordUserRepository;
        this.configModel = configModel;
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.discordUserCacheService = discordUserCacheService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AbstractResultDto completeRegistration(DiscordRegisterDto discordRegisterDto){
        DiscordUserEntity discordUserEntity = discordUserRepository.findByToken(discordRegisterDto.getToken());
        if(discordUserEntity == null){
            return FailedResultDto.getInstance();
        }

        boolean changed = false;

        if(discordRegisterDto.getGame().toUpperCase().equals("IW4") && discordUserEntity.getIw4madminMw2ClientId() == null){
            discordUserEntity.setIw4madminMw2ClientId(discordRegisterDto.getClientId());
            discordUserEntity.setMw2Name(GameUtil.cleanColors(discordRegisterDto.getPlayerName()));
            String guid = getGUID(discordRegisterDto.getClientId());
            discordUserEntity.setMw2Guid(guid);
            changed = true;
        }

        if(discordRegisterDto.getGame().toUpperCase().equals("T6") && discordUserEntity.getIw4madminBo2ClientId() == null){
            discordUserEntity.setIw4madminBo2ClientId(discordRegisterDto.getClientId());
            discordUserEntity.setBo2Name(GameUtil.cleanColors(discordRegisterDto.getPlayerName()));
            String guid = getGUID(discordRegisterDto.getClientId());
            guid = String.valueOf(Integer.parseInt(guid,16));
            discordUserEntity.setBo2Guid(guid);
            changed = true;
        }

        boolean success = false;

        try {
            if(changed){
                discordUserRepository.save(discordUserEntity);
                success = true;
                applicationEventPublisher.publishEvent(new DiscordPlayerRegisterEvent(this, discordUserEntity, discordRegisterDto.getGame().toUpperCase().equals("IW4") ? "mw2" : "bo2"));
                User jdaUser = getJDAUser(discordUserEntity);
                if(jdaUser != null){
                    jdaUser.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(getMessage(discordRegisterDto))).queue();
                }
            }
        }catch (Exception e){
            log.error("Failed to update discord user and send discord pm.", e);
        }
        if(success)
            return new SuccessResultDto();
        return FailedResultDto.getInstance();
    }

    private User getJDAUser(DiscordUserEntity discordUserEntity) {
        User jdaUser = jda.getUserById(discordUserEntity.getUserId());
        if(jdaUser == null){
            jdaUser = discordUserCacheService.getUserAndRemove(Long.parseLong(discordUserEntity.getUserId()));
        }
        return jdaUser;
    }

    private String getMessage(DiscordRegisterDto discordRegisterDto) {
        String message = new String(configModel.getMessages().get("registrationComplete"));
        return message.replace("$game", discordRegisterDto.getGame()).replace("$playerName", discordRegisterDto.getPlayerName());
    }

    private String getGUID(String clientId) {
        String name = iw4MAdminApiService.getClientStats(clientId).get(0).getName(); //first extract client name
        for (Iw4madminApiModel.BasicClient client : iw4MAdminApiService.findClient(name).getClients()) { //then loop over clients with such name
            if(client.getClientId() == Integer.parseInt(clientId)) //choose client with matching client id
                return client.getXuid(); //return his xuid
        }

        throw new RuntimeException("Failed to get guid");
    }
}
