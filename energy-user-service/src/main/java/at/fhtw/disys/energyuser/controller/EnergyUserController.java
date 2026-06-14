package at.fhtw.disys.energyuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyUserController {

    @GetMapping("/health")
    public String health() {
        return "Energy user service is running";
    }
}
