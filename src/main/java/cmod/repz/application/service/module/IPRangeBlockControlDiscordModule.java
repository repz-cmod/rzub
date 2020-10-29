package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.IPRangeBlockEntity;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.IPRangeBlockManagerService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import cmod.repz.application.util.MathUtil;
import com.google.common.net.InetAddresses;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@DiscordListenerComponent(command = "ipb", description = "Not available", hidden = true)
public class IPRangeBlockControlDiscordModule implements DiscordCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final IPRangeBlockManagerService ipRangeBlockManagerService;
    private final ConfigModel configModel;

    public IPRangeBlockControlDiscordModule(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, IPRangeBlockManagerService ipRangeBlockManagerService, ConfigModel configModel) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
        this.configModel = configModel;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);
        if(args.length < 1){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments. Try `!ipb help`. This command is only available for admins").complete(), 30);
        }else if(hasAccess(event.getMember())){
            String command = args[0];
            switch (command) {
                case "add":
                    if(args.length > 4){
                        if(InetAddresses.isInetAddress(args[1]) && InetAddresses.isInetAddress(args[2])){
                            Integer duration = Integer.parseInt(args[3]);
                            String reason = DiscordUtil.argumentsAsOne(Arrays.copyOfRange(args, 4, args.length));
                            Date date = new Date();
                            ipRangeBlockManagerService.add(IPRangeBlockEntity.builder()
                                    .startLong(MathUtil.ipToLong(InetAddresses.forString(args[1])))
                                    .endLong(MathUtil.ipToLong(InetAddresses.forString(args[2])))
                                    .start(args[1])
                                    .end(args[2])
                                    .reason(reason)
                                    .creationDate(date)
                                    .expiration(new Date(date.getTime() + (duration * 24 * 60 * 60 * 1000)))
                                    .username(event.getMember().getEffectiveName())
                                    .build());
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Added ip address range to blocking range for "+duration+" days.").complete(), 30);
                        }else {
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid IP Addresses").complete(), 30);
                        }
                    }else {
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments for adding to ban list. Try `!ipb help`.").complete(), 30);
                    }
                    break;
                case "list":
                    int page = 0;
                    if(args.length > 1){
                        page = Integer.parseInt(args[1]);
                    }
                    List<IPRangeBlockEntity> ipRangeBlockEntities = ipRangeBlockManagerService.get(page);
                    if(ipRangeBlockEntities.size() == 0){
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("List is empty ATM.").complete(), 30);
                    }else {
                        MessageEmbed messageEmbed = DiscordUtil.getBlockedIpRange(ipRangeBlockEntities, page, ipRangeBlockManagerService.count());
                        //Remove results after 60 seconds unless arg 2 is passed as seconds to keep this results
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(messageEmbed).complete(), args.length > 2 ? Integer.parseInt(args[2]) : 60);
                    }
                    break;
                case "rm":
                    if(args.length > 1){
                        int id = Integer.parseInt(args[1]);
                        ipRangeBlockManagerService.remove(id);
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Removed item").complete(), 30);
                    }
                    break;
                case "test":
                    if(args.length > 1){
                        String testIp = args[1];
                        boolean b = ipRangeBlockManagerService.shouldBlock(testIp);
                        String mid = b ? "is" : "is NOT";
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("IP `"+testIp+"` " + mid + " in block range").complete(), 20);
                    }
                    break;
                default:
                    String message = "`!ipb add <start> <end> <duration=5> <reason>` to add new block item. duration is number in `day`.\n" +
                            "`!ipb list <page=1> <autoDeleteDelay=60>` to list blocks.\n" +
                            "`!ipb test <ip>` to test ip in block range.\n" +
                            "`!ipb rm <id>` to delete an ip ban.";
                    discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(message).complete(), 20);
                    break;
            }
        }else {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Congrats! You have discovered a hidden command! Now, go away!").complete(), 20);
        }
    }

    private boolean hasAccess(Member member) {
        return configModel.getDiscord().getIpb().contains(member.getId());
    }
}
