package com.github.rzub.model.event;

import lombok.Getter;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiscordMemberJoinEvent extends ApplicationEvent {
    private final GuildMemberJoinEvent guildMemberJoinEvent;

    public DiscordMemberJoinEvent(Object source, GuildMemberJoinEvent guildMemberJoinEvent) {
        super(source);
        this.guildMemberJoinEvent = guildMemberJoinEvent;
    }
}
