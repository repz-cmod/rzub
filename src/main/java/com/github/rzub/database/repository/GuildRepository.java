package com.github.rzub.database.repository;

import net.dv8tion.jda.api.entities.Guild;

public interface GuildRepository {
    void setGuild(Guild guild);
    Guild getGuild();
}
