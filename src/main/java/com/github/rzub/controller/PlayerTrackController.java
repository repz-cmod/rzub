package com.github.rzub.controller;

import com.github.rzub.database.AnalyticsDao;
import com.github.rzub.database.repository.WhitelistRepository;
import com.github.rzub.model.dto.PlayerTackDto;
import com.github.rzub.model.dto.TrackResponseDto;
import com.github.rzub.service.IPRangeBlockManagerService;
import com.github.rzub.service.IPRegionBlockManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/plugin/v1")
public class PlayerTrackController {
    private final AnalyticsDao analyticsDao;
    private final IPRangeBlockManagerService ipRangeBlockManagerService;
    private final IPRegionBlockManagerService ipRegionBlockManagerService;
    private final WhitelistRepository whitelistRepository;

    @Autowired
    public PlayerTrackController(AnalyticsDao analyticsDao, IPRangeBlockManagerService ipRangeBlockManagerService, IPRegionBlockManagerService ipRegionBlockManagerService, WhitelistRepository whitelistRepository) {
        this.analyticsDao = analyticsDao;
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
        this.ipRegionBlockManagerService = ipRegionBlockManagerService;
        this.whitelistRepository = whitelistRepository;
    }

    @PostMapping("/client/join")
    public @ResponseBody
    TrackResponseDto playerJoin(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerJoined(playerTackDto.getServerIdAsLong(), playerTackDto.getClientId(), playerTackDto.getTrackerId());
        boolean shouldBlock = !whitelistRepository.existsByClientId(playerTackDto.getClientId()) && (ipRangeBlockManagerService.shouldBlock(playerTackDto.getIp()) || ipRegionBlockManagerService.shouldBlock(playerTackDto.getIp()));
        return new TrackResponseDto(shouldBlock);
    }

    @PostMapping("/client/leave")
    public @ResponseBody
    TrackResponseDto playerLeft(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerLeft(playerTackDto.getServerIdAsLong(), playerTackDto.getClientId(), playerTackDto.getTrackerId());
        return new TrackResponseDto(false);
    }

}
