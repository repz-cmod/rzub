package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.ClientEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2ClientRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2ClientRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.model.dto.AbstractResultDto;
import cmod.repz.application.model.dto.DiscordRegisterDto;
import cmod.repz.application.model.dto.FailedResultDto;
import cmod.repz.application.model.dto.SuccessResultDto;
import cmod.repz.application.service.DiscordUserCache;
import cmod.repz.application.service.api.IW4AdminApi;
import cmod.repz.application.util.GameUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Objects;

@Component
@Slf4j
public class CompleteRegisterComponent {
    private final JDA jda;
    private final DiscordUserRepository discordUserRepository;
    private final XlrMw2ClientRepository xlrMw2ClientRepository;
    private final XlrBo2ClientRepository xlrBo2ClientRepository;
    private final ConfigModel configModel;
    private final IW4AdminApi iw4AdminApi;
    private final DiscordUserCache discordUserCache;

    @Autowired
    public CompleteRegisterComponent(JDA jda, DiscordUserRepository discordUserRepository, XlrMw2ClientRepository xlrMw2ClientRepository, XlrBo2ClientRepository xlrBo2ClientRepository, ConfigModel configModel, IW4AdminApi iw4AdminApi, DiscordUserCache discordUserCache) {
        this.jda = jda;
        this.discordUserRepository = discordUserRepository;
        this.xlrMw2ClientRepository = xlrMw2ClientRepository;
        this.xlrBo2ClientRepository = xlrBo2ClientRepository;
        this.configModel = configModel;
        this.iw4AdminApi = iw4AdminApi;
        this.discordUserCache = discordUserCache;
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
            String guid = getGUID(discordRegisterDto.getClientId(), discordRegisterDto.getPlayerName());
            discordUserEntity.setMw2Guid(guid);
            ClientEntity clientEntity = xlrMw2ClientRepository.findByGuidLike(guid);
            if(clientEntity != null){
                discordUserEntity.setB3MW2ClientId(String.valueOf(clientEntity.getId()));
            }
            changed = true;
        }

        if(discordRegisterDto.getGame().toUpperCase().equals("T6") && discordUserEntity.getIw4adminBo2ClientId() == null){
            discordUserEntity.setIw4adminBo2ClientId(discordRegisterDto.getClientId());
            discordUserEntity.setBo2Name(GameUtil.cleanColors(discordRegisterDto.getPlayerName()));
            String guid = getGUID(discordRegisterDto.getClientId(), discordRegisterDto.getPlayerName());
            guid = String.valueOf(Integer.parseInt(guid,16));
            discordUserEntity.setBo2Guid(guid);
            ClientEntity clientEntity = xlrBo2ClientRepository.findByGuid(guid);
            if(clientEntity != null){
                discordUserEntity.setB3BO2ClientId(String.valueOf(clientEntity.getId()));
            }
            changed = true;
        }

        boolean success = false;

        try {
            if(changed){
                discordUserRepository.save(discordUserEntity);
                success = true;
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

    private String getGUID(String clientId, String name) {
        for (Iw4adminApiModel.BasicClient client : iw4AdminApi.findClient(name).getClients()) {
            if(client.getClientId() == Integer.parseInt(clientId))
                return client.getXuid();
        }

        throw new RuntimeException("Failed to get guid");
    }
}
