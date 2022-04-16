package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Document("ipbwhitelist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistEntity {
    private int id;
    private Integer clientId;
    private String username;
    private Date creationDate;
}
