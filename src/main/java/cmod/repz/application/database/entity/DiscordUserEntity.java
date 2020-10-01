package cmod.repz.application.database.entity;

import lombok.*;

import javax.persistence.*;

import static cmod.repz.application.config.ColumnName.DISCORD_USER_ID;
import static cmod.repz.application.config.ColumnName.IW4ADMIN_CLIENT_ID;
import static cmod.repz.application.config.ColumnName.PLAYER_NAME;
import static cmod.repz.application.config.TableName.DISCORD_USER;

@Entity
@Table(name = DISCORD_USER, indexes = {
        @Index(name = DISCORD_USER_ID, columnList = DISCORD_USER_ID, unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DiscordUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private long id;
    @Column(name = DISCORD_USER_ID, nullable = false)
    private String userId;
    @Column(name = IW4ADMIN_CLIENT_ID)
    private String iw4adminClientId;
    @Column(name = PLAYER_NAME)
    private String clientName;
}
