package com.analysetool.api;

import com.analysetool.modells.SystemLoad;
import com.analysetool.services.SystemLoadService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = {"/systemLoad", "/0wB4P2mly-xaRmeeDOj0_g/systemLoad"})
public class SystemLoadController {

    private final SystemLoadService systemLoadService;

    @Autowired
    public SystemLoadController(SystemLoadService systemLoadService) {
        this.systemLoadService = systemLoadService;
    }

    @GetMapping("/getHour")
    public int getHour() {
        return LocalDateTime.now().getHour();
    }

    @GetMapping("/average")
    public String getAverageLoad() {
        List<SystemLoad> allSystemLoads = systemLoadService.getAllSystemLoads();
        OptionalDouble averageCpuLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getCpuLoad).average();
        OptionalDouble averageMemoryLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getMemoryLoad).average();

        return String.format("{\"cpu\": %s, \"memory\": %s}", averageCpuLoad.orElse(0), averageMemoryLoad.orElse(0));
    }

    @GetMapping("/current")
    public String getCurrentLoad() throws JSONException {
        SystemLoad systemLoadNow = systemLoadService.getNow();
        if (systemLoadNow == null) return "{}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpu", systemLoadNow.getCpuLoad());
        jsonObject.put("memory", systemLoadNow.getMemoryLoad());
        jsonObject.put("networkSent", systemLoadNow.getNetworkSent());
        jsonObject.put("networkRecv", systemLoadNow.getNetworkRecv());

        return jsonObject.toString();
    }

    @GetMapping("/peak")
    public String getPeakLoad() {
        List<SystemLoad> allSystemLoads = systemLoadService.getAllSystemLoads();
        if (allSystemLoads.isEmpty()) return "{}";

        SystemLoad peakLoad = allSystemLoads.stream()
                .max(Comparator.comparingDouble(load -> (load.getCpuLoad() + load.getMemoryLoad()) / 2))
                .orElseThrow();

        return String.format("{\"cpu\": %f, \"memory\": %f, \"timestamp\": %d}",
                peakLoad.getCpuLoad(), peakLoad.getMemoryLoad(), peakLoad.getTimestamp());
    }

    @GetMapping("/systemLive")
    public String getComplete() throws JSONException {
        List<SystemLoad> allSystemLoads = systemLoadService.getTop60ByTimeDesc();
        Collections.reverse(allSystemLoads);
        if (allSystemLoads.isEmpty()) return "{}";
        JSONObject jsonObject = new JSONObject();
        List<Double> array_cpu = new ArrayList<>();
        List<Double> array_memory = new ArrayList<>();

        for (SystemLoad systemLoad : allSystemLoads) {
            array_cpu.add(systemLoad.getCpuLoad());
            array_memory.add(systemLoad.getMemoryLoad());
        }
        jsonObject.put("cpu_record", new JSONArray(array_cpu));
        jsonObject.put("memory_record",  new JSONArray(array_memory));

        return jsonObject.toString();
    }

    /**
     * Berechnet die Festplattennutzung in Prozent.
     * Diese Methode summiert den Gesamtspeicherplatz und den verfügbaren Speicherplatz
     * aller Laufwerke auf dem System und berechnet den Prozentsatz des genutzten
     * Speicherplatzes. Dies kann nützlich sein, um einen Überblick über die
     * Speicherauslastung des Systems zu erhalten.
     *
     * @return Die aktuelle Festplattennutzung in Prozent.
     */
    @GetMapping("/getDiscUsageInPercentage")
    public String getDiscUsageInPercentage(){
       return String.valueOf(systemLoadService.getDiskUsagePercentage());
    }

    /**
     * Diese Methode liefert Informationen über den Speicherplatz auf der Festplatte, einschließlich
     * des gesamten Speicherplatzes, des genutzten Speicherplatzes, des freien Speicherplatzes und der
     * prozentualen Nutzung der Festplatte. Die Daten werden im JSON-Format zurückgegeben.
     *
     * @return Eine JSON-String-Repräsentation der Festplattendaten oder "Error" bei Auftreten von Fehlern.
     */
    @GetMapping("/getDiscData")
    public String getDiscData(){
        try {
        JSONObject obj = new JSONObject();
        obj.put("total disc space",systemLoadService.getTotalDiskSpace());
        obj.put("used disc Space", systemLoadService.getUsedDiskSpace());
        obj.put("free disc Space",systemLoadService.getUsableDiskSpace());
        obj.put("disc usage in percentage",systemLoadService.getDiskUsagePercentage());
        return obj.toString();}
        catch(Exception e){
            return "Error";
        }
    }
}

