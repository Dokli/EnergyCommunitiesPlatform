package at.fhtw.disys.energycommunitiesplatformrestapi.controller;

import at.fhtw.disys.energycommunitiesplatformrestapi.dto.WeatherDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WeatherController_Example {
    private final List<WeatherDto> weatherDtoList = new ArrayList<>(
            List.of(
                    new WeatherDto( 1,"New York", new int[]{3, 4}),
                    new WeatherDto( 2,"California",new int[]{23, 4}),
                    new WeatherDto( 3,"Germany",new int[]{1, 4})
            )
    );

    @GetMapping("/weather")
    public List<WeatherDto> getWeather(@RequestParam(required = false) String city) {
        return weatherDtoList.stream().filter((weatherDto -> weatherDto.getCity().equals(city))).collect(Collectors.toList());
    }

    @GetMapping("/weather/current")
    public WeatherDto getCurrentWeather(@RequestParam String city) {
        return null;
    }


    @PostMapping("/observations")
    public void postWeather(@RequestBody List<WeatherDto> weatherDtos) {
        weatherDtoList.addAll(weatherDtos);
    }

}

