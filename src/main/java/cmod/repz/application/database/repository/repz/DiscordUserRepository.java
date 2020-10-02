package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.DiscordUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscordUserRepository extends JpaRepository<DiscordUserEntity, Long> {
    DiscordUserEntity findByUserId(String userId);
}
