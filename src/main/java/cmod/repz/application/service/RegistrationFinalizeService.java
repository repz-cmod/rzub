package cmod.repz.application.service;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.model.dto.AbstractResultDto;
import cmod.repz.application.model.dto.DiscordRegisterDto;
import cmod.repz.application.model.dto.FailedResultDto;
import cmod.repz.application.model.dto.SuccessResultDto;
import cmod.repz.application.model.event.DiscordPlayerRegisterEvent;
import cmod.repz.application.service.api.IW4AdminApi;
import cmod.repz.application.util.GameUtil;
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
    private final IW4AdminApi iw4AdminApi;
    private final DiscordUserCache discordUserCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public RegistrationFinalizeService(JDA jda, DiscordUserRepository discordUserRepository, ConfigModel configModel, IW4AdminApi iw4AdminApi, DiscordUserCache discordUserCache, ApplicationEventPublisher applicationEventPublisher) {
        this.jda = jda;
        this.discordUserRepository = discordUserRepository;
        this.configModel = configModel;
        this.iw4AdminApi = iw4AdminApi;
        this.discordUserCache = discordUserCache;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AbstractResultDto completeRegistration(DiscordRegisterDto discordRegisterDto){
        DiscordUserEntity discordUserEntity = discordUserRepository.findByToken(discordRegisterDto.getToken());
        if(discordUserEntity == null){
            return FailedResultDto.getInstance();
        }

        boolean changed = false;

        if(discordRegisterDto.getGame().toUpperCase().equals("IW4") && discordUserEntity.getIw4adminMw2ClientId() == null){
            discordUserEntity.setIw4adminMw2ClientId(discordRegisterDto.getClientId());
            discordUserEntity.setMw2Name(GameUtil.cleanColors(discordRegisterDto.getPlayerName()));
            String guid = getGUID(discordRegisterDto.getClientId());
            discordUserEntity.setMw2Guid(guid);
            changed = true;
        }

        if(discordRegisterDto.getGame().toUpperCase().equals("T6") && discordUserEntity.getIw4adminBo2ClientId() == null){
            discordUserEntity.setIw4adminBo2ClientId(discordRegisterDto.getClientId());
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
            jdaUser = discordUserCache.getUserAndRemove(Long.parseLong(discordUserEntity.getUserId()));
        }
        return jdaUser;
    }

    private String getMessage(DiscordRegisterDto discordRegisterDto) {
        String message = new String(configModel.getMessages().get("registrationComplete"));
        return message.replace("$game", discordRegisterDto.getGame()).replace("$playerName", discordRegisterDto.getPlayerName());
    }

    private String getGUID(String clientId) {
        String name = iw4AdminApi.getClientStats(clientId).get(0).getName(); //first extract client name
        for (Iw4adminApiModel.BasicClient client : iw4AdminApi.findClient(name).getClients()) { //then loop over clients with such name
            if(client.getClientId() == Integer.parseInt(clientId)) //choose client with matching client id
                return client.getXuid(); //return his xuid
        }

        throw new RuntimeException("Failed to get guid");
    }
}