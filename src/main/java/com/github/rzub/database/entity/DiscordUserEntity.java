package com.github.rzub.database.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static com.github.rzub.config.ColumnName.*;
import static com.github.rzub.config.TableName.DISCORD_USER;

@Entity
@Table(name = DISCORD_USER, indexes = {
        @Index(name = DISCORD_USER_ID, columnList = DISCORD_USER_ID, unique = true),
        @Index(name = "token", columnList = "token", unique = true),
        @Index(name = "b3_id", columnList = B3_MW2_CLIENT_ID+","+B3_BO2_CLIENT_ID)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DiscordUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private long id;
    @Column(name = DISCORD_USER_ID, nullable = false)
    private String userId;
    private String nickname;
    private String username;

    @Column(name = IW4MADMIN_MW2_CLIENT_ID)
    private String iw4madminMw2ClientId;
    @Column(name = IW4MADMIN_BO2_CLIENT_ID)
    private String iw4madminBo2ClientId;
    @Column(name = B3_MW2_CLIENT_ID)
    private String b3MW2ClientId;
    @Column(name = B3_BO2_CLIENT_ID)
    private String b3BO2ClientId;
    private String mw2Guid;
    private String bo2Guid;
    private String mw2Name;
    private String bo2Name;
    @Column(name = "token", nullable = false)
    private String token;
    private Date creationDate;
    private boolean messageSent;
    @OneToOne
    @JoinColumn(name="config_id")
    private DiscordUserConfigEntity config;
}
