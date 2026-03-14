package at.fhtw.disys.currentpercentage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentPercentageController {

    @GetMapping("/api/usage/current")
    public String getCurrentUsage() {
        return "Usage service läuft";
    }
}
