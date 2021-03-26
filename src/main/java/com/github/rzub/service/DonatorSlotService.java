package com.github.rzub.service;

import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.service.api.IW4MAdminApiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DonatorSlotService {
    private final IW4MAdminApiService iw4MAdminApiService;
    private final CookieRepository cookieRepository;
    private final SettingsModel settingsModel;
    private final CacheManager donatorTicketCacheManager;

    public Result emptySlot(String donatorDiscordUserId, String serverId){
        Iw4madminApiModel.Server chosenServer = iw4MAdminApiService.getServerList().stream().filter(server -> String.valueOf(server.getId()).equals(serverId)).collect(Collectors.toList()).get(0);
        if(chosenServer == null){
            return Result.error("Could not find a server with provided ID");
        }
        if(chosenServer.getMaxPlayers() != chosenServer.getCurrentPlayers())
            return Result.error("There is already a slot available in that server");
        if(hasTickets(donatorDiscordUserId))
            return Result.error("You got no tickets left. Try again later");

        chosenServer.getPlayers().sort(Comparator.comparing(Iw4madminApiModel.Player::getConnectionTime));
        Iw4madminApiModel.Player playerToKick = getNoneAdminPlayer(chosenServer.getPlayers());
        if(kick(serverId, playerToKick))
            updateTicket(donatorDiscordUserId);
        else return Result.error("Something went wrong, Please try again later");
        return Result.success(chosenServer.getName());
    }

    private boolean kick(String serverId, Iw4madminApiModel.Player playerToKick) {
        String cmd = "!kick " + playerToKick.getClientNumber() + " " + settingsModel.getMessages().get("donatorSlotKickReason");
        return iw4MAdminApiService.sendCommand(serverId, cmd, cookieRepository);
    }

    private void updateTicket(String donatorDiscordUserId) {
        Objects.requireNonNull(donatorTicketCacheManager.getCache("TICKET")).put(donatorDiscordUserId, "");
    }

    private boolean hasTickets(String donatorDiscordUserId) {
        return Objects.requireNonNull(donatorTicketCacheManager.getCache("TICKET")).get(donatorDiscordUserId) != null;
    }

    private Iw4madminApiModel.Player getNoneAdminPlayer(List<Iw4madminApiModel.Player> players) {
        for (Iw4madminApiModel.Player player : players) {
            if(!isAdmin(player))
                return player;
        }
        return Iw4madminApiModel.Player.builder().clientNumber(1000).build();
    }

    private boolean isAdmin(Iw4madminApiModel.Player player) {
        return !player.getLevel().equals("User");
    }


    @Getter
    @Setter
    @Builder
    public static class Result {
        private boolean success;
        private String error;
        private String serverName;
        private String kickedPlayerName;

        public static Result success(String serverName){
            return Result.builder()
                    .serverName(serverName)
                    .success(true)
                    .build();
        }

        public static Result error(String err){
            return Result.builder()
                    .success(false)
                    .error(err)
                    .build();
        }

    }

}
