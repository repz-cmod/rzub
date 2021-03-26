package com.github.rzub.controller;

import com.github.rzub.model.dto.AbstractResultDto;
import com.github.rzub.model.dto.DiscordRegisterDto;
import com.github.rzub.service.RegistrationFinalizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//todo add security support

@RestController
@RequestMapping("/plugin/v1")
public class PlayerRegistrationController {
    private final RegistrationFinalizeService registrationFinalizeService;

    @Autowired
    public PlayerRegistrationController(RegistrationFinalizeService registrationFinalizeService) {
        this.registrationFinalizeService = registrationFinalizeService;
    }

    @PostMapping("/discord/register")
    public @ResponseBody
    AbstractResultDto finalizeRegistration(@Valid @RequestBody DiscordRegisterDto discordRegisterDto){
        return registrationFinalizeService.completeRegistration(discordRegisterDto);
    }

}
