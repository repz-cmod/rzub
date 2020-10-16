package cmod.repz.application.database.entity.repz;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static cmod.repz.application.config.ColumnName.TOP_10_CHANNEL_NOTIF_SENT;
import static cmod.repz.application.config.TableName.DISCORD_USER_CONFIG;

@Table(name = DISCORD_USER_CONFIG)
@Entity
@Getter
@Setter
public class DiscordUserConfigEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;

    @Column(name = TOP_10_CHANNEL_NOTIF_SENT)
    private boolean top10ChannelNotifSent;
}
