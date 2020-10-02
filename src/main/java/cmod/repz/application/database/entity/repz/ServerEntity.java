package cmod.repz.application.database.entity.repz;

import cmod.repz.application.config.ColumnName;
import lombok.*;

import javax.persistence.*;

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
@Builder
public class ServerEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(name = ColumnName.SERVER_ID, nullable = false)
    private String serverId;
    @Column(name = ColumnName.SERVER_NAME, nullable = false)
    private String name;
    @Column
    private int port;
    @Column
    private String game;
}
