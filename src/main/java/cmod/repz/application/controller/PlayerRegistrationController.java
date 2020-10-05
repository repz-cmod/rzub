package cmod.repz.application.controller;

import cmod.repz.application.model.dto.AbstractResultDto;
import cmod.repz.application.model.dto.DiscordRegisterDto;
import cmod.repz.application.service.module.CompleteRegisterComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//todo add security support

@RestController
@RequestMapping("/plugin/v1")
public class PlayerRegistrationController {
    private final CompleteRegisterComponent completeRegisterComponent;

    @Autowired
    public PlayerRegistrationController(CompleteRegisterComponent completeRegisterComponent) {
        this.completeRegisterComponent = completeRegisterComponent;
    }

    @PostMapping("/discord/register")
    public @ResponseBody
    AbstractResultDto finalizeRegistration(@Valid @RequestBody DiscordRegisterDto discordRegisterDto){
        return completeRegisterComponent.completeRegistration(discordRegisterDto);
    }

}
