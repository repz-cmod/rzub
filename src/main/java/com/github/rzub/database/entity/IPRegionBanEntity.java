package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("ipRegionBans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPRegionBanEntity implements BasicIPBanInfo{
    @Id
    private String id;
    private String json;
    private int blockHash;
    private String info;
    private String reason;
    private String username;
    private Date expiration;
    private Date creation;
}
