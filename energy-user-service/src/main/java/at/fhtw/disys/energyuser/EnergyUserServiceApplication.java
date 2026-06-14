package at.fhtw.disys.energyuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EnergyUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyUserServiceApplication.class, args);
    }
}
