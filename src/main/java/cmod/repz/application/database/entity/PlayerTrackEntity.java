package cmod.repz.application.database.entity;

import cmod.repz.application.config.ColumnName;
import lombok.*;
import javax.persistence.*;
import java.util.Date;

import static cmod.repz.application.config.ColumnName.TRACKER_ID;
import static cmod.repz.application.config.TableName.PLAYER_TRACK_TABLE;

@Entity
@Table(name = PLAYER_TRACK_TABLE, indexes = {
        @Index(name = ColumnName.SERVER_ID, columnList = ColumnName.SERVER_ID ),
        @Index(name = ColumnName.CLIENT_ID, columnList = ColumnName.CLIENT_ID ),
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PlayerTrackEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(name = ColumnName.SERVER_ID, nullable = false)
    private String serverId;
    @Column(name = ColumnName.CLIENT_ID, nullable = false)
    private String clientId;
    @Column(name = ColumnName.PLAYER_NAME, nullable = false)
    private String playerName;
    @Column
    private Date joinDate;
    @Column
    private Date leftDate;
    @Column(name = TRACKER_ID)
    private String trackerId;
}
