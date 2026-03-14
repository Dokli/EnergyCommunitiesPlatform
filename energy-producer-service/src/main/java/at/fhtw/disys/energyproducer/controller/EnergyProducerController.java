package at.fhtw.disys.energyproducer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyProducerController {

    @GetMapping("/api/usage/current")
    public String getCurrentUsage() {
        return "Usage service läuft";
    }
}
