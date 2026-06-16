package at.fhtw.disys.energycommunitiesplatformrestapi.controller;

import at.fhtw.disys.energycommunitiesplatformrestapi.dto.CurrentPercentageDto;
import at.fhtw.disys.energycommunitiesplatformrestapi.dto.EnergyDataDto;
import at.fhtw.disys.energycommunitiesplatformrestapi.entity.CurrentPercentage;
import at.fhtw.disys.energycommunitiesplatformrestapi.entity.HourlyUsage;
import at.fhtw.disys.energycommunitiesplatformrestapi.repository.CurrentPercentageRepository;
import at.fhtw.disys.energycommunitiesplatformrestapi.repository.HourlyUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/energy")
public class EnergyCommunitiesPlatformRestApiController {

    private final CurrentPercentageRepository currentPercentageRepository;
    private final HourlyUsageRepository hourlyUsageRepository;

    @GetMapping("/current")
    public CurrentPercentageDto getCurrentHour() {
        CurrentPercentage currentPercentage = currentPercentageRepository.findTopByOrderByHourDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No current percentage data available"));

        return new CurrentPercentageDto(
                currentPercentage.getHour().toString(),
                currentPercentage.getCommunityDepleted(),
                currentPercentage.getGridPortion()
        );
    }

    @GetMapping("/historical")
    public List<EnergyDataDto> getHistoricData(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        try {
            LocalDateTime startDate = parseDateTime(start, false);
            LocalDateTime endDate = parseDateTime(end, true);

            if (startDate.isAfter(endDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before end");
            }

            return hourlyUsageRepository.findByHourBetweenOrderByHourAsc(startDate, endDate)
                    .stream()
                    .map(this::toEnergyDataDto)
                    .toList();
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date format must be ISO LocalDateTime");
        }
    }

    private LocalDateTime parseDateTime(String value, boolean endOfDay) {
        String trimmed = value.strip();
        if (trimmed.length() == 10) {
            LocalDate date = LocalDate.parse(trimmed);
            return endOfDay ? date.atTime(LocalTime.MAX) : date.atStartOfDay();
        }
        return LocalDateTime.parse(trimmed);
    }

    private EnergyDataDto toEnergyDataDto(HourlyUsage usage) {
        double consumed = usage.getCommunityUsed() + usage.getGridUsed();

        return new EnergyDataDto(
                usage.getHour().toString(),
                usage.getCommunityProduced(),
                consumed,
                usage.getCommunityUsed(),
                usage.getGridUsed()
        );
    }
}
