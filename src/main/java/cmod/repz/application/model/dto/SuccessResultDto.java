package cmod.repz.application.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResultDto {
    private String status = "ok";
    private final static SuccessResultDto successResultDto = new SuccessResultDto();

    public static SuccessResultDto getInstance(){
        return successResultDto;
    }
}
