package at.fhtw.disys.currentpercentage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsageController {

    @GetMapping("/health")
    public String health() {
        return "Usage service is running";
    }
}
