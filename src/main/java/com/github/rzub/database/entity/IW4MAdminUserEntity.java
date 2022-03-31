package com.github.rzub.database.entity;

import com.github.rzub.database.repository.CookieRepository;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "iw4madmin_user", indexes = {
        @Index(name = "client_id", columnList = "client_id", unique = true),
        @Index(name = "game", columnList = "game")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class IW4MAdminUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private long id;

    private String game;

    private String name;
    @Column(name = "client_id")
    private Long clientId;
    private String guid;

    private boolean done;
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name="discord_user_id", nullable=false)
    private DiscordUserEntity discordUser;

    @OneToOne
    @JoinColumn(name="cookie_id", nullable=false)
    private CookieEntity cookie;

}
