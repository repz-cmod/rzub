package com.github.rzub.database.entity;

import com.github.rzub.config.ColumnName;
import com.github.rzub.config.TableName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = TableName.DISCORD_USER_CONFIG)
@Entity
@Getter
@Setter
public class DiscordUserConfigEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;

    @Column(name = ColumnName.TOP_10_CHANNEL_NOTIF_SENT)
    private boolean top10ChannelNotifSent;
}
