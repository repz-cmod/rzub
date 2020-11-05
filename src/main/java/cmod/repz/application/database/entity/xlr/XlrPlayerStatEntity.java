package cmod.repz.application.database.entity.xlr;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "xlr_playerstats")
@Getter
@Setter
public class XlrPlayerStatEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;
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
    @OneToOne
    @JoinColumn(name="client_id")
    private ClientEntity client;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlrPlayerStatEntity that = (XlrPlayerStatEntity) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "XlrPlayerStatEntity{" +
                "id=" + id +
                ", ratio=" + ratio +
                ", skill=" + skill +
                ", client=" + client +
                '}';
    }
}
