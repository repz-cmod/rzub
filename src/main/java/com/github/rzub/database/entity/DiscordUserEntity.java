package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.github.rzub.config.ColumnName.DISCORD_USER_ID;

@Document("discordusers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordUserEntity {
    @Id
    private long id;
    private String userId;
    private String nickname;
    private String username;
    private String token;
    private Date creationDate;
    private boolean messageSent;


    @Override
    public String toString() {
        return "DiscordUserEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", token='" + token + '\'' +
                ", creationDate=" + creationDate +
                ", messageSent=" + messageSent +
                '}';
    }
}
