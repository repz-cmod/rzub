package com.github.rzub.database.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static com.github.rzub.config.ColumnName.DISCORD_USER_ID;
import static com.github.rzub.config.TableName.DISCORD_USER;

@Entity
@Table(name = DISCORD_USER, indexes = {
        @Index(name = DISCORD_USER_ID, columnList = DISCORD_USER_ID, unique = true),
        @Index(name = "token", columnList = "token", unique = true)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscordUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private long id;
    @Column(name = DISCORD_USER_ID, nullable = false)
    private String userId;
    private String nickname;
    private String username;
    @Column(name = "token", nullable = false)
    private String token;
    private Date creationDate;
    private boolean messageSent;

    @OneToMany(mappedBy = "discordUser", fetch = FetchType.EAGER)
    private List<IW4MAdminUserEntity> iw4MAdminUserEntities;

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
