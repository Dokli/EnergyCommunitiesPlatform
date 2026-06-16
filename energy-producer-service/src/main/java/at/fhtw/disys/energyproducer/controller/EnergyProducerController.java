package at.fhtw.disys.energyproducer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnergyProducerController {

    @GetMapping("/health")
    public String health() {
        return "Energy producer service is running";
    }
}
