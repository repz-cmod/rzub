package com.github.rzub.database.entity;

import com.github.rzub.config.ColumnName;
import com.github.rzub.config.TableName;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Table(name = TableName.IP_RANGE_BLOCK, indexes = {
        @Index(name = "range_index", columnList = ColumnName.IPB_START_LONG+","+ ColumnName.IPB_END_LONG, unique = true),
        @Index(name = "duration_index", columnList = "expiration"),

})
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPRangeBlockEntity  implements BasicIPBanInfo{
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private Integer id;
    @Column
    public String start;
    @Column(name = ColumnName.IPB_START_LONG)
    public long startLong;
    @Column
    public String end;
    @Column(name = ColumnName.IPB_END_LONG)
    public long endLong;
    @Column
    public Date creationDate;
    @Column
    public String username;
    @Column
    public String reason;
    @Column
    public Date expiration;
}
