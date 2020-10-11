package cmod.repz.application.controller;

import cmod.repz.application.service.module.XlrTop3PlainTextModule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class XlrController {
    private final XlrTop3PlainTextModule xlrTop3PlainTextModule;

    public XlrController(XlrTop3PlainTextModule xlrTop3PlainTextModule) {
        this.xlrTop3PlainTextModule = xlrTop3PlainTextModule;
    }

    @GetMapping("/xlr/top/3")
    public String getXlrTopPlayers(){
        return xlrTop3PlainTextModule.getTop3PlayersStats();
    }
}
