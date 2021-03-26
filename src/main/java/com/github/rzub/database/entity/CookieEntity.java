package com.github.rzub.database.entity;

import com.github.rzub.config.TableName;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = TableName.IW_COOKIE)
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
