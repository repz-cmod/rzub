package cmod.repz.application.model.dto;

import lombok.Getter;

@Getter
public class TrackResponseDto extends SuccessResultDto {
    private final boolean ban;

    public TrackResponseDto(boolean ban) {
        this.ban = ban;
    }
}
