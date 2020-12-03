package cmod.repz.application.database.entity.repz;

import cmod.repz.application.config.TableName;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static cmod.repz.application.config.ColumnName.IPB_END_LONG;
import static cmod.repz.application.config.ColumnName.IPB_START_LONG;

@Table(name = TableName.IP_RANGE_BLOCK, indexes = {
        @Index(name = "range_index", columnList = IPB_START_LONG+","+ IPB_END_LONG, unique = true),
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
    @Column(name = IPB_START_LONG)
    public long startLong;
    @Column
    public String end;
    @Column(name = IPB_END_LONG)
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
