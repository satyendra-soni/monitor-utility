package com.adennet.controller;

import com.adennet.config.ServerRegistry;
import com.adennet.dto.ServerDetail;
import com.adennet.dto.SessionCount;
import com.adennet.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(("/api"))
@RequiredArgsConstructor
public class MonitoringController {
    private final MonitoringService monitoringService;
    private final ServerRegistry serverRegistry;

    @GetMapping("/diskInfo")
    public List<Map<String, String>> getDiskInfo() {

        Map<String, ServerDetail> serverinfo = serverRegistry.getServerinfo();
        return serverinfo.keySet()
                .stream()
                .map(server -> monitoringService.diskDetails(server, serverinfo.get(server)))
                .collect(Collectors.toList());
    }

    @GetMapping("activeSession")
    public SessionCount activeSession(@RequestParam("isValid") boolean isValid) {
        return monitoringService.activeSession(isValid);
    }

    @GetMapping("triggerConsumptionMatch")
    public String triggerConsumptionMatch() {
        monitoringService.performDataMatching();
        return "CONSUMPTION_USAGES_MATCHING_JOB_STARTED";
    }

}
