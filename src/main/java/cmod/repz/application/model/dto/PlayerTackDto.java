package cmod.repz.application.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PlayerTackDto {
    @NotNull
    @NotEmpty
    private Integer clientId;
    @NotNull
    @NotEmpty
    private String ip;
    @NotNull
    @NotEmpty
    private Long trackerId;
    @NotNull
    @NotEmpty
    private Long serverId;
}
