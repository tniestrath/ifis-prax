package com.analysetool.api;

import com.analysetool.modells.SystemLoad;
import com.analysetool.services.SystemLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.*;

@RestController
@RequestMapping("/api/systemLoad")
public class SystemLoadController {

    private final SystemLoadService systemLoadService;

    @Autowired
    public SystemLoadController(SystemLoadService systemLoadService) {
        this.systemLoadService = systemLoadService;
    }

    @GetMapping("/average")
    public String getAverageLoad() {
        List<SystemLoad> allSystemLoads = systemLoadService.getAllSystemLoads();
        OptionalDouble averageCpuLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getCpuLoad).average();
        OptionalDouble averageMemoryLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getMemoryLoad).average();

        return String.format("{\"cpu\": %s, \"memory\": %s}", averageCpuLoad.orElse(0), averageMemoryLoad.orElse(0));
    }

    @GetMapping("/current")
    public String getCurrentLoad() {
        List<SystemLoad> allSystemLoads = systemLoadService.getAllSystemLoads();
        if (allSystemLoads.isEmpty()) return "{}";

        SystemLoad currentLoad = allSystemLoads.get(allSystemLoads.size() - 1);
        return String.format("{\"cpu\": %f, \"memory\": %f, \"timestamp\": %d}",
                currentLoad.getCpuLoad(), currentLoad.getMemoryLoad(), currentLoad.getTimestamp());
    }

    @GetMapping("/peak")
    public String getPeakLoad() {
        List<SystemLoad> allSystemLoads = systemLoadService.getAllSystemLoads();
        if (allSystemLoads.isEmpty()) return "{}";

        SystemLoad peakLoad = allSystemLoads.stream()
                .max((load1, load2) -> Double.compare(
                        (load1.getCpuLoad() + load1.getMemoryLoad()) / 2,
                        (load2.getCpuLoad() + load2.getMemoryLoad()) / 2))
                .orElseThrow();

        return String.format("{\"cpu\": %f, \"memory\": %f, \"timestamp\": %d}",
                peakLoad.getCpuLoad(), peakLoad.getMemoryLoad(), peakLoad.getTimestamp());
    }
}

