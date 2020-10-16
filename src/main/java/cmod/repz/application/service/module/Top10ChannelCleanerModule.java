package cmod.repz.application.service.module;

import cmod.repz.application.annotation.DiscordListenerComponent;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.service.DiscordDelayedMessageRemoverService;
import cmod.repz.application.service.listener.DiscordMessageListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

@DiscordListenerComponent
public class Top10ChannelCleanerModule implements DiscordMessageListener {
    private final DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService;
    private final String channelId;

    public Top10ChannelCleanerModule(ConfigModel configModel, DiscordDelayedMessageRemoverService discordDelayedMessageRemoverService) {
        this.discordDelayedMessageRemoverService = discordDelayedMessageRemoverService;
        channelId = configModel.getDiscord().getChannels().get("topPlayers");
    }

    @Override
    public void onMessage(GuildMessageReceivedEvent event) {
        if(event.getChannel().getId().equals(channelId) && event.getMessage().getMentionedMembers().size() == 0){
            discordDelayedMessageRemoverService.scheduleRemove(event.getMessage(), 120);
        }
    }
}
