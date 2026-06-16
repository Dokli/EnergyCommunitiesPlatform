package at.fhtw.disys.currentpercentage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentPercentageController {

    @GetMapping("/health")
    public String health() {
        return "Current percentage service is running";
    }
}
