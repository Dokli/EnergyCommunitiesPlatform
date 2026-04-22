package at.fhtw.disys.uijavafx.service;

import at.fhtw.disys.uijavafx.model.EnergyDataDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EnergyRestClientService {

    private static final String CURRENT_HOUR_URL = "http://localhost:8082/api/current-hour";
    private static final String HISTORICAL_DATA_URL = "http://localhost:8082/energy/historical";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EnergyRestClientService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public EnergyDataDto getCurrentHourData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CURRENT_HOUR_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), EnergyDataDto.class);
    }

    public List<EnergyDataDto> getHistoricalData(String from, String to) throws IOException, InterruptedException {
        String url = HISTORICAL_DATA_URL + "?start=" + from + "&end=" + to;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(
                response.body(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EnergyDataDto.class)
        );
    }
}