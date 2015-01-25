package bv.frootr.web;

import bv.frootr.model.FruitCount;
import bv.frootr.service.FrootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrootController {

    @Autowired
    private FrootService frootService;

    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @MessageMapping("/refresh")
    @SendTo("/topic/bowlupdates")
    public FruitCount greeting() throws Exception {
        return frootService.getFruitCount();
    }
}
