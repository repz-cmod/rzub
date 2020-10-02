package cmod.repz.application.database.entity.repz;

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
    @Column(name = ColumnName.PLAYER_NAME)
    private String playerName;
    @Column(name = ColumnName.JOIN_DATE)
    private Date joinDate;
    @Column(name = ColumnName.LEAVE_DATE)
    private Date leaveDate;
    @Column(name = TRACKER_ID)
    private String trackerId;
}
