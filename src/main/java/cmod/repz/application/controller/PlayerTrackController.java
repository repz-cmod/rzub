package cmod.repz.application.controller;

import cmod.repz.application.database.AnalyticsDao;
import cmod.repz.application.model.dto.PlayerTackDto;
import cmod.repz.application.model.dto.SuccessResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PlayerTrackController {
    private final AnalyticsDao analyticsDao;

    @Autowired
    public PlayerTrackController(AnalyticsDao analyticsDao) {
        this.analyticsDao = analyticsDao;
    }

    @PostMapping("/client/join")
    public @ResponseBody
    SuccessResultDto playerJoin(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerJoined(playerTackDto.getServerId(), playerTackDto.getClientId(), playerTackDto.getName(), playerTackDto.getTrackerId());
        return SuccessResultDto.getInstance();
    }

    @PostMapping("/client/leave")
    public @ResponseBody
    SuccessResultDto playerLeft(@RequestBody @Valid PlayerTackDto playerTackDto){
        analyticsDao.playerLeft(playerTackDto.getServerId(), playerTackDto.getClientId(), playerTackDto.getTrackerId());
        return SuccessResultDto.getInstance();
    }

}
