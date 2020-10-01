package cmod.repz.application.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IW4AdminStatResult {
    private double kd;
    private List<MapRanking> rankings;
    private String clientId;

    @Getter
    @Setter
    @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MapRanking {
        private String map;
        private String game;
        private int rank;

    }
}
