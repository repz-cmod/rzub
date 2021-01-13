package cmod.repz.application.database.entity.repz;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static cmod.repz.application.config.TableName.IW_COOKIE;

@Entity
@Table(name = IW_COOKIE)
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
    @Column(name = "cookie", length = 100000, columnDefinition = "text")
    @Type(type="text")
    private String cookie;
}
