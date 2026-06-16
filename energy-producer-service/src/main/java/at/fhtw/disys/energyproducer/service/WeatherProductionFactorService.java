package at.fhtw.disys.energyproducer.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeatherProductionFactorService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final double latitude = 48.2082;
    private final double longitude = 16.3738;

    public double currentSunFactor() {
        try {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current=cloud_cover,is_day";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return fallbackSunFactor();
            }

            double cloudCover = readNumber(response.body(), "cloud_cover", 50);
            double isDay = readNumber(response.body(), "is_day", 1);
            if (isDay < 1) {
                return 0.15;
            }
            return clamp(1.0 - (cloudCover / 100.0), 0.25, 1.0);
        } catch (Exception ignored) {
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

    private double readNumber(String json, String field, double fallback) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*([0-9.]+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return fallback;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
