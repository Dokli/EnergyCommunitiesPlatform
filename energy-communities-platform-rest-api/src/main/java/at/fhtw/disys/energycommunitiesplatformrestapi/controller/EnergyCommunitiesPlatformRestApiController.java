package at.fhtw.disys.energycommunitiesplatformrestapi.controller;

import at.fhtw.disys.energycommunitiesplatformrestapi.service.EnergyCalculationService;
import at.fhtw.disys.energycommunitiesplatformrestapi.dto.EnergyDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EnergyCommunitiesPlatformRestApiController {

    private final EnergyCalculationService calculationService;

    @GetMapping("/current-hour")
    public EnergyDataDto getCurrentHour() {
        double produced = 120;
        double consumed = 90;

        return createData(
                LocalDateTime.now().toString(),
                produced,
                consumed
        );
    }

    @GetMapping("/historic")
    public List<EnergyDataDto> getHistoricData(
            @RequestParam String from,
            @RequestParam String to
    ) {

        return List.of(
                createData("2026-04-21T10:00:00", 80, 100),
                createData("2026-04-21T11:00:00", 120, 110),
                createData("2026-04-21T12:00:00", 150, 100),
                createData("2026-04-21T13:00:00", 90, 130)
        );
    }

    private EnergyDataDto createData(String timestamp, double produced, double consumed) {

        double selfConsumed = calculationService.calculateSelfConsumed(produced, consumed);
        double gridImport = calculationService.calculateGridImport(produced, consumed);
        double gridExport = calculationService.calculateGridExport(produced, consumed);

        return new EnergyDataDto(
                timestamp,
                produced,
                consumed,
                selfConsumed,
                gridImport,
                gridExport
        );
    }
}
