package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cookies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CookieEntity {
    @Id
    private String id;
    private String cookie;

    @DBRef(lazy = true)
    private IW4MAdminUserEntity iw4MAdminUser;
}
