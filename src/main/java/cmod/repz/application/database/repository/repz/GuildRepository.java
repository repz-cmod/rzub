package cmod.repz.application.database.repository.repz;

import net.dv8tion.jda.api.entities.Guild;

public interface GuildRepository {
    void setGuild(Guild guild);
    Guild getGuild();
}
