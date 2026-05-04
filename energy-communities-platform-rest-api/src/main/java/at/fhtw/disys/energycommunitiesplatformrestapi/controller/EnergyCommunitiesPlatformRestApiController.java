package at.fhtw.disys.energycommunitiesplatformrestapi.controller;

import at.fhtw.disys.energycommunitiesplatformrestapi.service.EnergyCalculationService;
import at.fhtw.disys.energycommunitiesplatformrestapi.dto.EnergyDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;




@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EnergyCommunitiesPlatformRestApiController {

    private final EnergyCalculationService calculationService;

    @GetMapping("/current-hour")
    public EnergyDataDto getCurrentHour() {

        try {
            Double produced = 120.0;
            Double consumed = 90.0;

            if (produced == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "produced - no data for current hour");
            }
            if (produced == null || consumed == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "consumed - no data for current hour");
            }


            return createData(
                    LocalDateTime.now().toString(),
                    produced,
                    consumed
            );
        } catch (Exception e) {
throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @GetMapping("/historic")
    public List<EnergyDataDto> getHistoricData(
            @RequestParam String from,
            @RequestParam String to
    ) {

        try {
            if (LocalDateTime.parse(from).isAfter(LocalDateTime.parse(to))) {
                throw new ResponseStatusException((HttpStatus.BAD_REQUEST), "invalid request, from is after to");
            }


            return List.of(
                    createData("2026-04-21T10:00:00", 80, 100),
                    createData("2026-04-21T11:00:00", 120, 110),
                    createData("2026-04-21T12:00:00", 150, 100),
                    createData("2026-04-21T13:00:00", 90, 130)
            );
        }
     catch (DateTimeException e){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateformat not valid");
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private EnergyDataDto createData(String timestamp, double produced, double consumed) {

        double selfConsumed = calculationService.calculateSelfConsumed(produced, consumed);
        double gridImport = calculationService.calculateGridImport(produced, consumed);
        double gridExport = calculationService.calculateGridExport(produced, consumed);

        return  new EnergyDataDto(
                timestamp,
                produced,
                consumed,
                selfConsumed,
                gridImport,
                gridExport
        );
    }
}
