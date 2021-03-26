package com.github.rzub.service.listener;

import com.github.rzub.service.CommandAccessService;
import com.github.rzub.service.DiscordDelayedMessageRemoverService;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class AbstractAuthorizedCommandListener implements DiscordCommandListener {
    private final CommandAccessService commandAccessService;
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;

    protected AbstractAuthorizedCommandListener(CommandAccessService commandAccessService, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.commandAccessService = commandAccessService;
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
    }

    @Override
    public void onCommand(GuildMessageReceivedEvent event, String[] args) {
        if (!commandAccessService.hasAccess(getCommandId(), event.getMember())) {
            discordDelayedMessageRemoverService.scheduleRemove(event.getChannel().sendMessage("You do not have an access to this command").complete(), 20);
        }
        this.onAuthorizedCommand(event, args);
    }

    protected abstract void onAuthorizedCommand(GuildMessageReceivedEvent event, String[] args);

}
