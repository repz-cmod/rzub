package com.github.rzub.service.discord;

import com.github.rzub.database.entity.DiscordUserEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import com.github.rzub.database.repository.DiscordUserRepository;
import com.github.rzub.service.api.IW4MAdminApiService;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;


@DiscordController
public class IW4MAdminCommandExecuteDiscordModule {
    private final IW4MAdminApiService iw4MAdminApiService;
    private final DiscordUserRepository discordUserRepository;


    public IW4MAdminCommandExecuteDiscordModule(IW4MAdminApiService iw4MAdminApiService, DiscordUserRepository discordUserRepository) {
        this.iw4MAdminApiService = iw4MAdminApiService;
        this.discordUserRepository = discordUserRepository;
    }

    @DiscordCommand(name = "botiwexec", description = "Executes command in iw4madmin as bot", ephemeralDiffer = true)
    public void onBotIWExec(@DiscordParameter(name = "server-id") String serverId, @DiscordParameter(name="command") String command) {
        IW4MAdminApiService.CommandResponse commandResponse = iw4MAdminApiService.execute(serverId, command);
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        event.getHook().sendMessage("Success: `" + commandResponse.isSuccess() +  "` | status: `" + commandResponse.getStatus() + "` | body: `" + getPartialBody(commandResponse.getBody()) + "`").queue();
    }

    @DiscordCommand(name = "iwexec", description = "Executes command in iw4madmin as bot", ephemeralDiffer = true)
    public void onUserIWExec(@DiscordParameter(name = "server-id") String serverId, @DiscordParameter(name="command") String command, @DiscordParameter(name = "client-id", required = false) Long clientId) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        Member eventMember = event.getMember();


        DiscordUserEntity discordUserEntity = discordUserRepository.findByUserId(Objects.requireNonNull(eventMember).getUser().getId());
        if(discordUserEntity == null) {
            event.getHook().sendMessage("You are not logged in.").queue();
            return;
        }

        Hibernate.initialize(discordUserEntity.getIw4MAdminUserEntities());
        List<IW4MAdminUserEntity> iw4MAdminUserEntities = discordUserEntity.getIw4MAdminUserEntities();
        if (iw4MAdminUserEntities.size() == 0){
            event.getHook().sendMessage("There are no IW4MAdmin matching to your discord in our DB").queue();
            return;
        }

        IW4MAdminUserEntity iw4MAdminUserEntity = null;
        if (clientId == null){
            iw4MAdminUserEntity = iw4MAdminUserEntities.get(0);
        }else {
            for (IW4MAdminUserEntity mAdminUserEntity : iw4MAdminUserEntities) {
                if (mAdminUserEntity.getClientId().equals(clientId)){
                    iw4MAdminUserEntity = mAdminUserEntity;
                    break;
                }
            }
            if (iw4MAdminUserEntity == null){
                event.getHook().sendMessage("You don't own the account with client id: "+ clientId).queue();
                return;
            }
        }

        IW4MAdminApiService.CommandResponse commandResponse = iw4MAdminApiService.execute(serverId, command, iw4MAdminUserEntity);
        event.getHook().sendMessage("Success: `" + commandResponse.isSuccess() +  "` | status: `" + commandResponse.getStatus() + "` | body: `" + getPartialBody(commandResponse.getBody()) + "`").queue();
    }

    private String getPartialBody(String body){
        if(body.length() < 100)
            return body;
        else {
            return body.substring(0, 100) + " ...";
        }
    }
}
