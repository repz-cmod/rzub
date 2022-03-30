package com.github.rzub.service.discord;

import com.github.rzub.service.DonatorSlotService;
import com.github.rzub.util.GameUtil;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@DiscordController
public class DonatorSlotDiscordModule {
    private final DonatorSlotService donatorSlotService;

    public DonatorSlotDiscordModule(DonatorSlotService donatorSlotService) {
        this.donatorSlotService = donatorSlotService;
    }

    @DiscordCommand(name = "join", description = "Creates a slot for donators in a specific server (by kicking last joined player)")
    public void onCommand(@DiscordParameter(name="server-id") String serverId) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        DonatorSlotService.Result result = donatorSlotService.emptySlot(event.getMember().getId(), serverId);
        if (result.isSuccess()) {
            event.getHook().sendMessage("A slot is now empty in \""+ GameUtil.cleanColors(result.getServerName()) +"\"").queue();
        }else {
            event.getHook().sendMessage("Failed. Reason: `"+result.getError()+"`").queue();
        }
    }

}
