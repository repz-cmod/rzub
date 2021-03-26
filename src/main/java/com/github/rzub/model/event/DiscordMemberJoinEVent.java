package com.github.rzub.model.event;

import lombok.Getter;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.springframework.context.ApplicationEvent;

@Getter
public class DiscordMemberJoinEVent extends ApplicationEvent {
    private final GuildMemberJoinEvent guildMemberJoinEvent;

    public DiscordMemberJoinEVent(Object source, GuildMemberJoinEvent guildMemberJoinEvent) {
        super(source);
        this.guildMemberJoinEvent = guildMemberJoinEvent;
    }
}
