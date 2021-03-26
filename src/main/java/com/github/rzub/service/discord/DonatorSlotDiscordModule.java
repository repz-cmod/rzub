package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.DonatorSlotService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import com.github.rzub.util.GameUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent(command = "join", description = "Creates a slot for donators in a specific server")
public class DonatorSlotDiscordModule extends AbstractAuthorizedCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final DonatorSlotService donatorSlotService;

    public DonatorSlotDiscordModule(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, DonatorSlotService donatorSlotService) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.donatorSlotService = donatorSlotService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 20);
        super.onCommand(event, args);
    }

    @Override
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {


        if(args.length < 1){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid Arguments. Try `!join <serverId>` and replace `<serverId>` with id of the server you want to join to. See servers and Ids: `!servers`").complete(), 30);
            return;
        }

        DonatorSlotService.Result result = donatorSlotService.emptySlot(event.getMember().getId(), args[0]);
        if (result.isSuccess()) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("A slot is now empty in \""+ GameUtil.cleanColors(result.getServerName()) +"\"").complete(), 30);
        }else {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Failed. Reason: `"+result.getError()+"`").complete(), 30);
        }
    }
}
