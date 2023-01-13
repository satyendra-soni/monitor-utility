package com.adennet.controller;

import com.adennet.dto.ServerDetail;
import com.adennet.dto.SessionCount;
import com.adennet.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(("/monitor"))
@RequiredArgsConstructor
public class MonitoringController {
    private final MonitoringService monitoringService;

    @PostMapping("/diskInfo")
    public List<Map<String, String>> getDiskInfo(@RequestBody Map<String, ServerDetail> serverDetail) {
        return serverDetail.keySet()
                .stream()
                .map(server -> monitoringService.diskDetails(server, serverDetail.get(server)))
                .collect(Collectors.toList());
    }

    @GetMapping("activeSession")
    public SessionCount activeSession(@RequestParam("isValid") boolean isValid) {
        log.info("Retrieving active session from radius sessions.");
        return monitoringService.activeSession(isValid);
    }

    @GetMapping("consumptionUsages")
    public void consumptionUsages() {
         monitoringService.getConsumptionDataFromPostgres();
    }

}
