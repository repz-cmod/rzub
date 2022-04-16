package com.github.rzub.service.discord;

import com.github.rzub.database.entity.IPRangeBlockEntity;
import com.github.rzub.service.IPRangeBlockManagerService;
import com.github.rzub.util.DiscordUtil;
import com.github.rzub.util.MathUtil;
import com.google.common.net.InetAddresses;
import io.github.sepgh.sbdiscord.annotations.DiscordCommand;
import io.github.sepgh.sbdiscord.annotations.DiscordController;
import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.command.context.CommandContextHolder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@DiscordController
public class IPRangeBlockControlDiscordModule {
    private final IPRangeBlockManagerService ipRangeBlockManagerService;

    public IPRangeBlockControlDiscordModule(IPRangeBlockManagerService ipRangeBlockManagerService) {
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
    }

    @DiscordCommand(name = "ipb-add", description = "Ban IP range")
    public void onAdd(
            @DiscordParameter(name="from") String ipFrom,
            @DiscordParameter(name="to") String ipTo,
            @DiscordParameter(name = "duration", description = "duration of block in days") Integer duration,
            @DiscordParameter(name = "reason", description = "reason of block") String reason
        ) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();

        if(InetAddresses.isInetAddress(ipFrom) && InetAddresses.isInetAddress(ipTo)){
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, duration);
            Date expiration = c.getTime();
            ipRangeBlockManagerService.add(IPRangeBlockEntity.builder()
                    .startLong(MathUtil.ipToLong(InetAddresses.forString(ipFrom)))
                    .endLong(MathUtil.ipToLong(InetAddresses.forString(ipTo)))
                    .start(ipFrom)
                    .end(ipTo)
                    .reason(reason)
                    .creationDate(date)
                    .expiration(expiration)
                    .username(event.getMember().getEffectiveName())
                    .build());
            event.getHook().sendMessage("Added ip address range to blocking range for "+duration+" days.").queue();
        }
    }

    @DiscordCommand(name = "ipb-list", description = "List IP Range blocks")
    public void onList(
            @DiscordParameter(name="page") Integer page
    ) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();

        List<IPRangeBlockEntity> ipRangeBlockEntities = ipRangeBlockManagerService.get(page);
        if(ipRangeBlockEntities.size() == 0){
            event.getHook().sendMessage("List is empty ATM.").queue();
        }else {
            MessageEmbed messageEmbed = DiscordUtil.getBlockedIpRange(ipRangeBlockEntities, page, ipRangeBlockManagerService.count());
            //Remove results after 60 seconds unless arg 2 is passed as seconds to keep this results
            event.getHook().sendMessageEmbeds(messageEmbed).queue();
        }
    }

    @DiscordCommand(name = "ipb-rm", description = "Remove IP Range block")
    public void onRemove(
            @DiscordParameter(name="id") String id
    ) {
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        ipRangeBlockManagerService.remove(id);
        event.getHook().sendMessage("Removed item").queue();
    }

    @DiscordCommand(name = "ipb-test", description = "Test IP Range block")
    public void onTest(
            @DiscordParameter(name="ip") String ip
    ) {
        boolean b = ipRangeBlockManagerService.shouldBlock(ip);
        String mid = b ? "is" : "is NOT";
        SlashCommandEvent event = CommandContextHolder.getContext().getSlashCommandEvent().get();
        event.getHook().sendMessage("IP `"+ip+"` " + mid + " in block range").queue();
    }
}
