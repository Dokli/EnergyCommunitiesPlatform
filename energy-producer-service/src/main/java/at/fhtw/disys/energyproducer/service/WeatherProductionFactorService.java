package at.fhtw.disys.energyproducer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class WeatherProductionFactorService {

    private final WebClient webClient;
    private final double latitude;
    private final double longitude;

    public WeatherProductionFactorService(
            @Value("${energy.producer.latitude}") double latitude,
            @Value("${energy.producer.longitude}") double longitude
    ) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double currentSunFactor() {
        try {
            Map<?, ?> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "cloud_cover,is_day")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(2));

            if (response == null || !(response.get("current") instanceof Map<?, ?> current)) {
                return fallbackSunFactor();
            }

            double cloudCover = number(current.get("cloud_cover"), 50);
            double isDay = number(current.get("is_day"), 1);
            if (isDay < 1) {
                return 0.15;
            }
            return clamp(1.0 - (cloudCover / 100.0), 0.25, 1.0);
        } catch (RuntimeException ignored) {
            return fallbackSunFactor();
        }
    }

    private double fallbackSunFactor() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 6 || hour > 20) {
            return 0.15;
        }
        List<Integer> highSunHours = List.of(10, 11, 12, 13, 14, 15);
        return highSunHours.contains(hour) ? 0.9 : 0.55;
    }

    private double number(Object value, double fallback) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return fallback;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
