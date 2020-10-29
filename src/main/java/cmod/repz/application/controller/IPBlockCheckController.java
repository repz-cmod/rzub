package cmod.repz.application.controller;

import cmod.repz.application.model.dto.AbstractResultDto;
import cmod.repz.application.model.dto.FailedResultDto;
import cmod.repz.application.model.dto.IpCheckDto;
import cmod.repz.application.model.dto.SuccessResultDto;
import cmod.repz.application.service.IPRangeBlockManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plugin/v1")
public class IPBlockCheckController {
    private final IPRangeBlockManagerService ipRangeBlockManagerService;

    @Autowired
    public IPBlockCheckController(IPRangeBlockManagerService ipRangeBlockManagerService) {
        this.ipRangeBlockManagerService = ipRangeBlockManagerService;
    }

    @PostMapping("/ipb/check")
    public @ResponseBody
    AbstractResultDto checkBlock(@RequestBody IpCheckDto ipCheckDto){
        boolean shouldBlock = ipRangeBlockManagerService.shouldBlock(ipCheckDto.getIp());
        if(shouldBlock){
            return new SuccessResultDto();
        }else {
            return new FailedResultDto();
        }
    }
}
