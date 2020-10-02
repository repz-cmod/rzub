package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.ClientEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2ClientRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.dto.DiscordRegisterDto;
import cmod.repz.application.model.dto.SuccessResultDto;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
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
    private final ConfigModel configModel;

    @Autowired
    public CompleteRegisterComponent(JDA jda, DiscordUserRepository discordUserRepository, XlrMw2ClientRepository xlrMw2ClientRepository, ConfigModel configModel) {
        this.jda = jda;
        this.discordUserRepository = discordUserRepository;
        this.xlrMw2ClientRepository = xlrMw2ClientRepository;
        this.configModel = configModel;
    }

    @Transactional
    public SuccessResultDto completeRegistration(DiscordRegisterDto discordRegisterDto){
        DiscordUserEntity discordUserEntity = discordUserRepository.findByToken(discordRegisterDto.getToken());
        if(discordUserEntity == null){
            throw new RuntimeException("Invalid Token");
        }

        if(discordRegisterDto.getGame().equals("IW4")){
            discordUserEntity.setMw2Name(discordRegisterDto.getPlayerName());
            discordUserEntity.setB3MW2ClientId(discordRegisterDto.getClientId());
            discordUserEntity.setMw2Guid(discordRegisterDto.getClientId());
            ClientEntity clientEntity = xlrMw2ClientRepository.findByGuid(discordRegisterDto.getXuid());
            if(clientEntity != null){
                discordUserEntity.setB3MW2ClientId(String.valueOf(clientEntity.getId()));
            }
        }

        //todo: bo2

        try {
            Objects.requireNonNull(jda.getUserById(discordUserEntity.getUserId())).openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(getMessage(discordRegisterDto))).queue();
        }catch (Exception e){
            log.error("Failed to send discord pm.", e);
        }
        return new SuccessResultDto();
    }

    private String getMessage(DiscordRegisterDto discordRegisterDto) {
        String message = new String(configModel.getMessages().get("registrationComplete"));
        return message.replace("$game", discordRegisterDto.getGame()).replace("$playerName", discordRegisterDto.getPlayerName());
    }
}
