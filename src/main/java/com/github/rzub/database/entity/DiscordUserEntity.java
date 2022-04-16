package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("discordusers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordUserEntity {
    @Id
    private String id;
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
