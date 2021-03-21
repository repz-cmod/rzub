package cmod.repz.application.database.entity.repz;

import cmod.repz.application.config.TableName;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static cmod.repz.application.config.ColumnName.RGB_BLOCK_HASH;

@Table(name = TableName.REGION_BLOCK, indexes = {
        @Index(name = "hash_index", columnList = RGB_BLOCK_HASH, unique = true),
        @Index(name = "duration_index", columnList = "expiration"),

})
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPRegionBanEntity implements BasicIPBanInfo{
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private Integer id;

    @Column(name = RGB_BLOCK_HASH)
    private int blockHash;
    @Column
    private String info;
    @Column
    private String reason;
    @Column
    private String username;
    @Column
    private Date expiration;
    @Column
    private Date creation;
}
