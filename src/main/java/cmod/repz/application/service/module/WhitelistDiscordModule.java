package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.database.entity.repz.IPRangeBlockEntity;
import cmod.repz.application.database.entity.repz.WhitelistEntity;
import cmod.repz.application.database.repository.repz.WhitelistRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordCommandListener;
import cmod.repz.application.util.DiscordUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

@DiscordListenerComponent(command = "whitelist", description = "Not available", hidden = true)
public class WhitelistDiscordModule implements DiscordCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final WhitelistRepository whitelistRepository;
    private final ConfigModel configModel;

    public WhitelistDiscordModule(DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, WhitelistRepository whitelistRepository, ConfigModel configModel) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.configModel = configModel;
        this.whitelistRepository = whitelistRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);
        String command = null;
        if(args.length < 1){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments. Try `!whitelist help`.").complete(), 30);
        }else if(hasAccess(event.getMember())){
            command = args[0];
            switch (command) {
                case "add":
                    if(args.length > 1){
                        Integer clientId = Integer.valueOf(args[1]);
                        try {
                            whitelistRepository.save(WhitelistEntity.builder()
                                    .clientId(clientId)
                                    .username(event.getMember().getEffectiveName())
                                    .creationDate(new Date())
                                    .build());
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Added client to whitelist.").complete(), 30);
                        }catch (Exception e){
                            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Failed: `"+e.getMessage()+"`").complete(), 30);
                        }
                    }else {
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments for adding to white list. Try `!whitelist help`.").complete(), 30);
                    }
                    break;
                case "test":
                    if(args.length > 1){
                        Integer clientId = Integer.valueOf(args[1]);
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(whitelistRepository.existsByClientId(clientId) ? "+" : "-" + " `" + clientId + "`").complete(), 30);
                    }
                    break;
                case "rm":
                    if(args.length > 1){
                        Integer clientId = Integer.valueOf(args[1]);
                        whitelistRepository.deleteByClientId(clientId);
                        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Removed item").complete(), 30);
                    }
                    break;
                default:
                    String message = "`!whitelist add <clientId>` to add new client to whitelist.\n" +
                            "`!whitelist test <clientId>` to see if clientId exists in whitelist.\n" +
                            "`!ipb rm <clientId>` to remove a client from whitelist.";
                    discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage(message).complete(), 20);
                    break;
            }
        }else {
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Congrats! You have discovered a hidden command! Now, go away!").complete(), 20);
        }
    }

    private boolean hasAccess(Member member) {
            String management = configModel.getDiscord().getRoles().get("management");
            String jmanagement = configModel.getDiscord().getRoles().get("jmanagement");
            for (Role role : member.getRoles()) {
                if(role.getId().equals(management) || role.getId().equals(jmanagement))
                    return true;
            }
        return false;
    }
}
