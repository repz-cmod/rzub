package cmod.repz.application.database.entity.repz;

import cmod.repz.application.config.ColumnName;
import lombok.*;

import javax.persistence.*;

import java.util.Date;

import static cmod.repz.application.config.TableName.PLAYER_TRACK_TABLE;

@Table(name = PLAYER_TRACK_TABLE, indexes = {
        @Index(name = ColumnName.CLIENT_ID, columnList = ColumnName.CLIENT_ID, unique = true)
})
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @Column(name = ColumnName.CLIENT_ID, nullable = false)
    private Integer clientId;
    private String username;
    private Date creationDate;
}
