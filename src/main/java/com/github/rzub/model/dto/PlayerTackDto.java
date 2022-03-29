package com.github.rzub.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class PlayerTackDto {
    @NotNull
    private Integer clientId;
    @NotNull
    private String ip;
    @NotNull
    private Long trackerId;
    @NotNull
    @NotEmpty
    private String serverId;

    public Long getServerIdAsLong(){
        return Long.parseLong(getServerId());
    }
}
