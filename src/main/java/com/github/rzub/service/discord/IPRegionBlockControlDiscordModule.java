package com.github.rzub.service.discord;

import com.github.rzub.database.entity.IPRegionBanEntity;
import com.github.rzub.service.IPRegionBlockManagerService;
import com.github.rzub.util.DiscordUtil;
import com.google.common.net.InetAddresses;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;


@DiscordController
public class IPRegionBlockControlDiscordModule {
    private final IPRegionBlockManagerService ipRegionBlockManagerService;

    public IPRegionBlockControlDiscordModule(IPRegionBlockManagerService ipRegionBlockManagerService) {
        this.ipRegionBlockManagerService = ipRegionBlockManagerService;
    }

    @DiscordCommand(name = "ipb2-add", description = "Add to IP Ban V2 (region based)")
    @SneakyThrows
    public void onAdd(@DiscordParameter(name = "ip") String ip,
                      @DiscordParameter(name = "duration") Integer duration,
                      @DiscordParameter(name = "reason") String reason) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        if(InetAddresses.isInetAddress(ip)) {
            String info = ipRegionBlockManagerService.add(ip, reason, event.getMember().getEffectiveName(), duration);
            event.getHook().sendMessage("Added ip address region to blocking list: `"+info+"`").queue();
        }else {
            event.getHook().sendMessage("Invalid IP Addresses").queue();
        }
    }

    @DiscordCommand(name = "ipb2-list", description = "List IP Ban V2 (region based)")
    public void onList(@DiscordParameter(name = "page") Integer page) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        List<IPRegionBanEntity> ipRangeBlockEntities = ipRegionBlockManagerService.get(page);
        if(ipRangeBlockEntities.size() == 0){
            event.getHook().sendMessage("List is empty ATM.").queue();
        }else {
            MessageEmbed messageEmbed = DiscordUtil.getBlockedIpRegion(ipRangeBlockEntities, page, ipRegionBlockManagerService.count());
            event.getHook().sendMessageEmbeds(messageEmbed).queue();
        }
    }

    @DiscordCommand(name = "ipb2-rm", description = "List IP Ban V2 (region based)")
    public void onRemove(@DiscordParameter(name = "page") Integer id) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        ipRegionBlockManagerService.remove(id);
        event.getHook().sendMessage("Item is removed").queue();
    }

    @DiscordCommand(name = "ipb2-test", description = "Test IP Ban V2 (region based)")
    public void onRemove(@DiscordParameter(name = "ip") String testIp) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        boolean b = ipRegionBlockManagerService.shouldBlock(testIp);
        String mid = b ? "is" : "is NOT";
        event.getHook().sendMessage("IP `"+testIp+"` " + mid + " in block range").queue();
    }

}
