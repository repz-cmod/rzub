package com.github.rzub.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractResultDto {
    private final String status;

    public AbstractResultDto(String status) {
        this.status = status;
    }
}
