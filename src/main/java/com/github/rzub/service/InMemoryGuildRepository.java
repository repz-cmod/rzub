package com.github.rzub.service;

import com.github.rzub.database.repository.GuildRepository;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryGuildRepository implements GuildRepository {
    private volatile Guild guild;

    @Override
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public Guild getGuild() {
        return this.guild;
    }
}
