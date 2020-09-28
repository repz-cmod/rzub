package cmod.repz.application.database.entity;

import cmod.repz.application.config.ColumnName;
import lombok.*;

import javax.persistence.*;

import static cmod.repz.application.config.ColumnName.MAX_PLAYERS;
import static cmod.repz.application.config.TableName.SERVER_TABLE;

@Entity
@Table(name = SERVER_TABLE, indexes = {
        @Index(name = ColumnName.SERVER_ID, columnList = ColumnName.SERVER_ID, unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServerEntity {
    @Id
    private long id;
    @Column(name = ColumnName.SERVER_ID, nullable = false)
    private String serverId;
    @Column
    private String name;
    @Column(name = MAX_PLAYERS)
    private int maxPlayers;
    @Column
    private int port;
    @Column
    private String game;
}
