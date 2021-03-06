package com.github.rzub.database.entity;

import com.github.rzub.config.ColumnName;
import com.github.rzub.config.TableName;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = TableName.SERVER_TRACK_TABLE, indexes = {
        @Index(name = ColumnName.SERVER_ID, columnList = ColumnName.SERVER_ID + ",date")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ServerTrackEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(name = ColumnName.SERVER_ID, nullable = false)
    private Long serverId;
    @Column
    private Date date;
    @Column(name = ColumnName.PLAYER_COUNT)
    private int playerCount;
    @Column(name = ColumnName.MAX_PLAYER_COUNT)
    private int maxPlayerCount;
    @Column
    private double percentage;
    @Column(name = ColumnName.MAP_NAME, nullable = false)
    private String mapName;
}
