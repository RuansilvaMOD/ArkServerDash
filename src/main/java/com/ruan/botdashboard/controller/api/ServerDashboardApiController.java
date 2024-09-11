package com.ruan.botdashboard.controller.api;

import com.ruan.botdashboard.dto.ServerDashboardRequestDTO;
import com.ruan.botdashboard.service.ServerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/serverDashboard")
@RequiredArgsConstructor
public class ServerDashboardApiController {

    private final ServerDashboardService serverDashboardService;

    @PostMapping("/register")
    public ResponseEntity<String> registerServerDashboard(@RequestBody ServerDashboardRequestDTO body) {
        return serverDashboardService.registerServerDashboard(body);
    }
}
