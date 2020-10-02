package cmod.repz.application.database.entity.xlr;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "xlr_playerstats")
@Getter
@Setter
public class XlrPlayerStatEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;
    @Column(name = "client_id")
    private int clientId;
    private int kills;
    private int deaths;
    @Column(name = "teamkills")
    private int teamKills;
    @Column(name = "teamdeaths")
    private int teamDeaths;
    private int suicides;
    private int ratio;
    private int skill;
    private int skills;
    @Column(name = "curstreak")
    private int currentStreak;
    @Column(name = "winstreak")
    private int winStreak;
    @Column(name = "losestreak")
    private int loseStreak;
    private int rounds;
}
