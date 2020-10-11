package cmod.repz.application.service;

import cmod.repz.application.database.repository.repz.GuildRepository;
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
