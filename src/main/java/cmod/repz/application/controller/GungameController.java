package cmod.repz.application.controller;

import cmod.repz.application.service.module.GungamePlayerModule;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GungameController {
    private final GungamePlayerModule gungamePlayerModule;

    public GungameController(GungamePlayerModule gungamePlayerModule) {
        this.gungamePlayerModule = gungamePlayerModule;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/api/gungame")
    public @ResponseBody
    String processRequest(@RequestParam("data") String data){
        return gungamePlayerModule.process(data);
    }

}
