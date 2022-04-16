package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("iw4madminUsers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class IW4MAdminUserEntity {
    private long id;
    private String game;
    private String name;
    private Long clientId;
    private String guid;
    private boolean done;
    private Date creationDate;
    @DBRef
    private DiscordUserEntity discordUser;
}
