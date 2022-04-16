package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("ipRangeBans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPRangeBlockEntity  implements BasicIPBanInfo{
    @Id
    private String id;
    public String start;
    public long startLong;
    public String end;
    public long endLong;
    public Date creationDate;
    public String username;
    public String reason;
    public Date expiration;
}
