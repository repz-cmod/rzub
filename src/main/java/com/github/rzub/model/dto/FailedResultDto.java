package com.github.rzub.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FailedResultDto extends AbstractResultDto{
    private final static FailedResultDto failedResultDto = new FailedResultDto();

    public FailedResultDto() {
        super("failed");
    }

    public static FailedResultDto getInstance(){
        return failedResultDto;
    }
}
