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
    private short teamKills;
    @Column(name = "teamdeaths")
    private short teamDeaths;
    private short suicides;
    private float ratio;
    private int skill;
    private short assists;
    @Column(name = "curstreak")
    private short currentStreak;
    @Column(name = "winstreak")
    private short winStreak;
    @Column(name = "losestreak")
    private short loseStreak;
    private int rounds;
}
