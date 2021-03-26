package com.github.rzub.database.entity;

import com.github.rzub.config.ColumnName;
import com.github.rzub.config.TableName;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Table(name = TableName.WHITE_LIST_TABLE, indexes = {
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
