package com.github.rzub.database.entity;

import com.github.rzub.config.ColumnName;
import com.github.rzub.config.TableName;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = TableName.SERVER_TABLE, indexes = {
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
    private long serverId;
    @Column(name = ColumnName.SERVER_NAME, nullable = false)
    private String name;
    @Column
    private int port;
    @Column
    private String game;
}
