package cmod.repz.application.database.entity.xlr;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "clients", schema = "xlr_mw2")
@Getter
@Setter
public class ClientEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE)
    private int id;
    @Column
    private String ip;
    @Column
    private int connections;
    @Column
    private String guid;
    @Column
    private String pbid;
    @Column
    private String name;

    @Override
    public String toString() {
        return "ClientEntity{" +
                "id=" + id +
                ", guid='" + guid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
