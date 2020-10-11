package cmod.repz.application.service.module;

import cmod.repz.application.config.DiscordStateHolder;
import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.repz.DiscordUserRepository;
import cmod.repz.application.model.event.XlrTopEvent;
import cmod.repz.application.service.Top10DiscordPrizeService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XlrTopPlayerDiscordRoleModule implements ApplicationListener<XlrTopEvent> {
    private final DiscordUserRepository discordUserRepository;
    private final Top10DiscordPrizeService top10DiscordPrizeService;
    private volatile XlrTopEvent previousEvent;

    @Autowired
    public XlrTopPlayerDiscordRoleModule(DiscordUserRepository discordUserRepository, Top10DiscordPrizeService top10DiscordPrizeService) {
        this.discordUserRepository = discordUserRepository;
        this.top10DiscordPrizeService = top10DiscordPrizeService;
    }

    @Override
    public void onApplicationEvent(@NotNull XlrTopEvent xlrTopEvent) {
        if(!DiscordStateHolder.isReady()){
            return;
        }

        /* this should be commented when not under develop: */

        if(previousEvent == null){
            previousEvent = xlrTopEvent;
            return;
        }
         /* ends here */

        List<String> mw2ClientsToAddRole = getClientsToAddRole(xlrTopEvent.getMw2Stats(), previousEvent != null ? previousEvent.getMw2Stats() : new ArrayList<>());
        List<String> mw2ClientsToRemoveRole = getClientsToRemoveRole(xlrTopEvent.getMw2Stats(), previousEvent != null ? previousEvent.getMw2Stats() : new ArrayList<>());

        List<String> bo2ClientsToAddRole = getClientsToAddRole(xlrTopEvent.getBo2Stats(), previousEvent != null ? previousEvent.getBo2Stats() : new ArrayList<>());
        List<String> bo2ClientsToRemoveRole = getClientsToRemoveRole(xlrTopEvent.getBo2Stats(), previousEvent != null ? previousEvent.getBo2Stats() : new ArrayList<>());

        handle("mw2", mw2ClientsToAddRole, mw2ClientsToRemoveRole);
        handle("bo2", bo2ClientsToAddRole, bo2ClientsToRemoveRole);

        previousEvent = xlrTopEvent;
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
            log.info(discordUserEntity.getUsername() + " is now a top player.");
            top10DiscordPrizeService.handleTop10Enter(game, discordUserEntity);
        });

        removes.forEach(discordUserEntity -> {
            top10DiscordPrizeService.handleTop10Leave(game, discordUserEntity);
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
}
