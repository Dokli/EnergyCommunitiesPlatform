package at.fhtw.disys.energyuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyUserController {

    @GetMapping("/api/usage/current")
    public String getCurrentUsage() {
        return "Usage service läuft";
    }
}
