package com.github.rzub.controller;

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
    private final IPRangeBlockManagerService ipRangeBlockManagerService;
    private final IPRegionBlockManagerService ipRegionBlockManagerService;
    private final WhitelistRepository whitelistRepository;

    @Autowired
    public PlayerTrackController(IPRangeBlockManagerService ipRangeBlockManagerService, IPRegionBlockManagerService ipRegionBlockManagerService, WhitelistRepository whitelistRepository) {
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
        this.ipRegionBlockManagerService = ipRegionBlockManagerService;
        this.whitelistRepository = whitelistRepository;
    }

    @PostMapping("/client/join")
    public @ResponseBody
    TrackResponseDto playerJoin(@RequestBody @Valid PlayerTackDto playerTackDto){
        boolean shouldBlock = !whitelistRepository.existsByClientId(playerTackDto.getClientId())
                && (
                        ipRangeBlockManagerService.shouldBlock(String.valueOf(playerTackDto.getClientId()), playerTackDto.getIp())
                                ||
                        ipRegionBlockManagerService.shouldBlock(String.valueOf(playerTackDto.getClientId()), playerTackDto.getIp()
                        )
        );
        return new TrackResponseDto(shouldBlock);
    }

}
