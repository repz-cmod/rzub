package cmod.repz.application.database.entity.repz;

import lombok.*;

import javax.persistence.*;

import static cmod.repz.application.config.TableName.DISCORD_USER;

@Entity
@Table(name = DISCORD_USER)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CookieEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private long id;

    private String cookie;
}
