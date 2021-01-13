package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.DonatorSlotService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.GameUtil;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Objects;

@AllArgsConstructor
@DiscordListenerComponent(command = "join", description = "Creates a slot for donators in a specific server")
public class DonatorSlotDiscordModule implements DiscordCommandListener {
    private final ConfigModel configModel;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final DonatorSlotService donatorSlotService;

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 20);

        if(!hasAccess(Objects.requireNonNull(event.getMember()))){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("This command is only accessible to donators. Donate to RepZ to unlock this.").complete(), 30);
            return;
        }

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

    private boolean hasAccess(Member member){
        for (Role role : member.getRoles()) {
            if(role.getId().equals(configModel.getDiscord().getRoles().get("donator")))
                return true;
        }
        return false;
    }
}
