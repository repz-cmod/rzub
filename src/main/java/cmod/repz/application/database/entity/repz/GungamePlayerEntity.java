package cmod.repz.application.database.entity.repz;

import lombok.*;

import javax.persistence.*;

@Table(name = "gungame_player", indexes = {
        @Index(name = "gungame_player_guid", columnList = "guid", unique = true),
        @Index(name = "gungame_player_wins", columnList = "wins")
})
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GungamePlayerEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;
    @Column
    private String guid;
    @Column
    private int wins = 0;
}
