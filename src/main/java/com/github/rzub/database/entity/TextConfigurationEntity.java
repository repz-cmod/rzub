package com.github.rzub.database.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("textConfigurations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextConfigurationEntity {
    private @Id String id;
    @Indexed(unique = true)
    private String name;
    private String value;

    public enum ConfigurationNames {
        DEMO_MESSAGE
    }
}
