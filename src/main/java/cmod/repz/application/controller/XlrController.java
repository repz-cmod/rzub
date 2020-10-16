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

    @GetMapping("/test/xlr/top/3")
    public String getTestXlrTopPlayers(){
        return "6ce149b860635f9f;;;2f076fe3091528eb;;;c4d3b9e7355c7af2";
    }
}
