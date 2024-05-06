package hw.prince.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hw.prince.service.InfoService;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/port")
    public String getPort() {
        return infoService.getCurrentPort();
    }
}