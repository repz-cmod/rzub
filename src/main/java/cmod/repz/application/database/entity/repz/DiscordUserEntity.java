package cmod.repz.application.database.entity.repz;

import lombok.*;

import javax.persistence.*;

import java.util.Date;

import static cmod.repz.application.config.ColumnName.DISCORD_USER_ID;
import static cmod.repz.application.config.ColumnName.IW4ADMIN_CLIENT_ID;
import static cmod.repz.application.config.ColumnName.B3_MW2_CLIENT_ID;
import static cmod.repz.application.config.ColumnName.B3_BO2_CLIENT_ID;
import static cmod.repz.application.config.ColumnName.B3_BF3_CLIENT_ID;
import static cmod.repz.application.config.ColumnName.PLAYER_NAME;
import static cmod.repz.application.config.TableName.DISCORD_USER;

@Entity
@Table(name = DISCORD_USER, indexes = {
        @Index(name = DISCORD_USER_ID, columnList = DISCORD_USER_ID, unique = true),
        @Index(name = "token", columnList = "token", unique = true)
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
    private String nickname;
    private String username;

    @Column(name = IW4ADMIN_CLIENT_ID)
    private String iw4adminClientId;
    @Column(name = B3_MW2_CLIENT_ID)
    private String b3MW2ClientId;
    @Column(name = B3_BO2_CLIENT_ID)
    private String b3BO2ClientId;
    private String mw2Guid;
    private String bo2Guid;
    private String mw2Name;
    private String bo2Name;
    @Column(name = "token", nullable = false)
    private String token;
    private Date creationDate;
    private boolean messageSent;
}
