package cmod.repz.application.database.repository.repz;

import cmod.repz.application.database.entity.repz.DiscordUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscordUserRepository extends JpaRepository<DiscordUserEntity, Long> {
    DiscordUserEntity findByUserId(String userId);
    DiscordUserEntity findByToken(String token);
    List<DiscordUserEntity> findAllByB3MW2ClientIdIn(List<String> clientIds);
    List<DiscordUserEntity> findAllByB3BO2ClientIdIn(List<String> clientIds);
}
