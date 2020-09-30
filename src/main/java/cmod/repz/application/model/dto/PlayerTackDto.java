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
    private String clientId;
    private String name;
    @NotNull
    @NotEmpty
    private String trackerId;
    @NotNull
    @NotEmpty
    private String serverId;
}
