package com.adennet.controller;

import com.adennet.dto.ServerDetail;
import com.adennet.dto.SessionCount;
import com.adennet.service.GrafanaMongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(("/api"))
@RequiredArgsConstructor
public class GrafanaMongoController {
    private final GrafanaMongoService grafanaMongoService;

    @PostMapping("/diskInfo")
    public List<Map<String, String>> getDiskInfo(@RequestBody Map<String, ServerDetail> serverDetail) {
        return serverDetail.keySet()
                .stream()
                .map(server -> grafanaMongoService.diskDetails(server, serverDetail.get(server)))
                .collect(Collectors.toList());
    }

    @GetMapping("activeSession")
    public SessionCount activeSession(@RequestParam("isValid") boolean isValid) {
        System.out.println("isValid = " + isValid);
        return grafanaMongoService.activeSession(isValid);
    }

}
