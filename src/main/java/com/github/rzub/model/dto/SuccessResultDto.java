package com.github.rzub.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResultDto extends AbstractResultDto{
    private final static SuccessResultDto successResultDto = new SuccessResultDto();

    public SuccessResultDto() {
        super("ok");
    }

    public static SuccessResultDto getInstance(){
        return successResultDto;
    }
}
