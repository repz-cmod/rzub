package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.entity.IPRegionBanEntity;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.IPRegionBlockManagerService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import com.github.rzub.util.DiscordUtil;
import com.google.common.net.InetAddresses;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

@DiscordListenerComponent(command = "ipb2", description = "Not available", hidden = true)
public class IPRegionBlockControlDiscordModule extends AbstractAuthorizedCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final IPRegionBlockManagerService ipRegionBlockManagerService;

    public IPRegionBlockControlDiscordModule(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, IPRegionBlockManagerService ipRegionBlockManagerService) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.ipRegionBlockManagerService = ipRegionBlockManagerService;
    }

    @SneakyThrows
    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);
        super.onCommand(event, args);
    }

    @Override
    @SneakyThrows
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {
        if(args.length < 1){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments. Try `!ipb2 help`.").complete(), 30);
        }else{
            String command = args[0];
            switch (command) {
                case "add":
                    if(args.length > 3){
                        if(InetAddresses.isInetAddress(args[1])){
                            Integer duration = Integer.parseInt(args[2]);
                            String reason = DiscordUtil.argumentsAsOne(Arrays.copyOfRange(args, 3, args.length));
                            String info = ipRegionBlockManagerService.add(args[1], reason, event.getMember().getEffectiveName(), duration);
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Added ip address region to blocking list: `"+info+"`").complete(), 30);
                        }else {
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid IP Addresses").complete(), 30);
                        }
                    }else {
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments for adding to ban list. Try `!ipb2 help`.").complete(), 30);
                    }
                    break;
                case "list":
                    int page = 0;
                    if(args.length > 1){
                        page = Integer.parseInt(args[1]);
                    }
                    List<IPRegionBanEntity> ipRangeBlockEntities = ipRegionBlockManagerService.get(page);
                    if(ipRangeBlockEntities.size() == 0){
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("List is empty ATM.").complete(), 30);
                    }else {
                        MessageEmbed messageEmbed = DiscordUtil.getBlockedIpRegion(ipRangeBlockEntities, page, ipRegionBlockManagerService.count());
                        //Remove results after 60 seconds unless arg 2 is passed as seconds to keep this results
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(messageEmbed).complete(), args.length > 2 ? Integer.parseInt(args[2]) : 60);
                    }
                    break;
                case "rm":
                    if(args.length > 1){
                        int id = Integer.parseInt(args[1]);
                        ipRegionBlockManagerService.remove(id);
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Removed item").complete(), 30);
                    }
                    break;
                case "test":
                    if(args.length > 1){
                        String testIp = args[1];
                        boolean b = ipRegionBlockManagerService.shouldBlock(testIp);
                        String mid = b ? "is" : "is NOT";
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("IP `"+testIp+"` " + mid + " in block range").complete(), 20);
                    }
                    break;
                default:
                    String message = "`!ipb2 add <ip> <duration> <reason>` to add new range block item. duration is number in `day`.\n" +
                            "`!ipb2 list <page=1> <autoDeleteDelay=60>` to list blocks.\n" +
                            "`!ipb2 test <ip>` to test ip in block region.\n" +
                            "`!ipb2 rm <id>` to delete a record";
                    discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(message).complete(), 20);
                    break;
            }
        }
    }
}
