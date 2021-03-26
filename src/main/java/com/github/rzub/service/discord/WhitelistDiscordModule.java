package com.github.rzub.service.discord;

import com.github.rzub.annotation.DiscordListenerComponent;
import com.github.rzub.database.entity.WhitelistEntity;
import com.github.rzub.database.repository.WhitelistRepository;
import com.github.rzub.model.SettingsModel;
import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import com.github.rzub.service.listener.AbstractAuthorizedCommandListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

@DiscordListenerComponent(command = "whitelist", description = "Not available", hidden = true)
public class WhitelistDiscordModule extends AbstractAuthorizedCommandListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final WhitelistRepository whitelistRepository;

    public WhitelistDiscordModule(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService, WhitelistRepository whitelistRepository, SettingsModel settingsModel) {
        super(commandAccessService, discordDelayedMessageRemoverService);
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        this.whitelistRepository = whitelistRepository;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 30);
        super.onCommand(event, args);
    }

    @Override
    protected void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args) {
        if(args.length < 1){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage().getChannel().sendMessage("Invalid arguments. Try `!whitelist help`.").complete(), 30);
        }else {
            String command = args[0];
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
        }
    }

}
