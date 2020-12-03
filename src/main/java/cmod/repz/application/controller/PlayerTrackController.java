package cmod.repz.application.controller;

import cmod.repz.application.database.AnalyticsDao;
import cmod.repz.application.model.dto.PlayerTackDto;
import cmod.repz.application.model.dto.TrackResponseDto;
import cmod.repz.application.service.IPRangeBlockManagerService;
import cmod.repz.application.service.IPRegionBlockManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PlayerTrackController {
    private final AnalyticsDao analyticsDao;
    private final IPRangeBlockManagerService ipRangeBlockManagerService;
    private final IPRegionBlockManagerService ipRegionBlockManagerService;

    @Autowired
    public PlayerTrackController(AnalyticsDao analyticsDao, IPRangeBlockManagerService ipRangeBlockManagerService, IPRegionBlockManagerService ipRegionBlockManagerService) {
        this.analyticsDao = analyticsDao;
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
        this.ipRegionBlockManagerService = ipRegionBlockManagerService;
    }

    @PostMapping("/client/join")
    public @ResponseBody
    TrackResponseDto playerJoin(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerJoined(playerTackDto.getServerIdAsLong(), playerTackDto.getClientId(), playerTackDto.getTrackerId());
        boolean shouldBlock = ipRangeBlockManagerService.shouldBlock(playerTackDto.getIp()) || ipRegionBlockManagerService.shouldBlock(playerTackDto.getIp());
        return new TrackResponseDto(shouldBlock);
    }

    @PostMapping("/client/leave")
    public @ResponseBody
    TrackResponseDto playerLeft(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerLeft(playerTackDto.getServerIdAsLong(), playerTackDto.getClientId(), playerTackDto.getTrackerId());
        return new TrackResponseDto(false);
    }

}
