package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.service.api.IW4AdminApi;
import cmod.repz.application.service.listener.DiscordCommandListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/*
 * Registers a player
 */
@DiscordListenerComponent(command = "register")
@Slf4j
public class UserRegistrationDiscordModule implements DiscordCommandListener {
    private final DiscordUserRepository discordUserRepository;
    private final IW4AdminApi iw4AdminApi;

    @Autowired
    public UserRegistrationDiscordModule(DiscordUserRepository discordUserRepository, IW4AdminApi iw4AdminApi) {
        this.discordUserRepository = discordUserRepository;
        this.iw4AdminApi = iw4AdminApi;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        MessageChannel messageChannel = event.getMessage().getChannel();
        try {
            if(args.length == 0) {
                messageChannel.sendMessage("Please provide valid arguments").complete();
            }else {
                String clientId = args[0];
                DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId());
                if(discordUserEntity == null){
                    discordUserEntity = new DiscordUserEntity();
                    discordUserEntity.setUserId(event.getMember().getUser().getId());
                }
                List<Iw4adminApiModel.Stat> clientStats = iw4AdminApi.getClientStats(clientId);
                discordUserEntity.setClientName(clientStats.get(0).getName());
                discordUserEntity.setIw4adminClientId(clientId);
                discordUserRepository.save(discordUserEntity);
                log.info("Added / Updated new discord user: " + discordUserEntity.toString());
                event.getMessage().addReaction("✅").complete();
            }
        }catch (Exception e){
            log.error("Failed to handle user registration", e);
        }
    }
}
