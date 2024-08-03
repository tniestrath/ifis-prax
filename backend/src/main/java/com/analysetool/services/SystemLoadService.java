package com.analysetool.services;

import com.analysetool.modells.SystemLoad;
import com.analysetool.repositories.SystemLoadRepository;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToLongFunction;

@Service
public class SystemLoadService {

    private final SystemLoadRepository systemLoadRepository;
    private final SystemInfo systemInfo = new SystemInfo();

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    @Autowired
    public SystemLoadService(SystemLoadRepository systemLoadRepository) {
        this.systemLoadRepository = systemLoadRepository;
    }

    @Scheduled(fixedRate = 60000) // 1 Minute
    public void recordSystemLoad() {
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        NetworkIF networkIF = systemInfo.getHardware().getNetworkIFs().get(0);

        long[] prevTicks = processor.getSystemCpuLoadTicks();

        double networkRecvOld = networkIF.getBytesRecv();
        double networkSentOld = networkIF.getBytesSent();

        try {
            // Pause zwischen den Messungen
            Thread.sleep(1000); // 1 Sekunde
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        GlobalMemory memory = hal.getMemory();
        double totalMemory = memory.getTotal();
        double availableMemory = memory.getAvailable();
        double memoryLoad = (totalMemory - availableMemory) / totalMemory;
        double networkRecv = networkIF.getBytesRecv() - networkRecvOld;
        double networkSent = networkIF.getBytesSent() - networkSentOld;


        SystemLoad systemLoad = new SystemLoad();
        systemLoad.setCpuLoad(cpuLoad);
        systemLoad.setMemoryLoad(memoryLoad);
        systemLoad.setTimestamp(System.currentTimeMillis());
        systemLoad.setNetworkRecv(networkRecv);
        systemLoad.setNetworkSent(networkSent);

        systemLoadRepository.save(systemLoad);
    }
    @Scheduled(cron = "0 0 0 * * ?") // Jeden Tag um Mitternacht
    @Transactional
    public void deleteOldRecords() {
        systemLoadRepository.deleteByTimestampBefore(System.currentTimeMillis() - (72 * 60 * 60 * 1000));// 72 Stunden in Millisekunden
    }


    public SystemLoad getNow() { return systemLoadRepository.getNetworkNow();}

    public List<SystemLoad> getAllSystemLoads() {
        return systemLoadRepository.findAll();
    }

    public List<SystemLoad> getTop60ByTimeDesc(){
        return systemLoadRepository.getTop60ByOrderByTimestampDesc();
    }
    

    /**
     * Calculates the disk usage as a percentage.
     *
     * @return the percentage of disk space used.
     */
    public double getDiskUsagePercentage() {
        long totalSpace = getTotalDiskSpace();
        long usableSpace = getUsableDiskSpace();
        long usedSpace = totalSpace - usableSpace;
        return (double) usedSpace / totalSpace ;
    }

    /**
     * Retrieves the total disk space across all file stores.
     *
     * @return total disk space in bytes.
     */
    public long getTotalDiskSpace() {
        return getDiskSpace(OSFileStore::getTotalSpace);
    }

    /**
     * Retrieves the total used disk space across all file stores.
     *
     * @return used disk space in bytes.
     */
    public long getUsedDiskSpace() {
        long totalSpace = getTotalDiskSpace();
        long usableSpace = getUsableDiskSpace();
        return totalSpace - usableSpace;
    }

    /**
     * Retrieves the total usable (free) disk space across all file stores.
     *
     * @return usable disk space in bytes.
     */
    public long getUsableDiskSpace() {
        return getDiskSpace(OSFileStore::getUsableSpace);
    }

    /**
     * Helper method to sum disk space based on a provided space calculator function.
     *
     * @param spaceCalculator a function defining how to calculate disk space for a file store.
     * @return the sum of the disk space calculated across all file stores.
     */
    private long getDiskSpace(ToLongFunction<OSFileStore> spaceCalculator) {
        FileSystem fileSystem = systemInfo.getOperatingSystem().getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        return fsArray.stream()
                .mapToLong(spaceCalculator)
                .sum();
    }


    public int getHour() {
        return LocalDateTime.now().getHour();
    }


    public String getAverageLoad() {
        List<SystemLoad> allSystemLoads = getAllSystemLoads();
        OptionalDouble averageCpuLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getCpuLoad).average();
        OptionalDouble averageMemoryLoad = allSystemLoads.stream().mapToDouble(SystemLoad::getMemoryLoad).average();

        return String.format("{\"cpu\": %s, \"memory\": %s}", averageCpuLoad.orElse(0), averageMemoryLoad.orElse(0));
    }

    public String getCurrentLoad() throws JSONException {
        SystemLoad systemLoadNow = getNow();
        if (systemLoadNow == null) return "{}";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cpu", systemLoadNow.getCpuLoad());
        jsonObject.put("memory", systemLoadNow.getMemoryLoad());
        jsonObject.put("networkSent", systemLoadNow.getNetworkSent());
        jsonObject.put("networkRecv", systemLoadNow.getNetworkRecv());

        return jsonObject.toString();
    }


    public String getPeakLoad() {
        List<SystemLoad> allSystemLoads = getAllSystemLoads();
        if (allSystemLoads.isEmpty()) return "{}";

        SystemLoad peakLoad = allSystemLoads.stream()
                .max(Comparator.comparingDouble(load -> (load.getCpuLoad() + load.getMemoryLoad()) / 2))
                .orElseThrow();

        return String.format("{\"cpu\": %f, \"memory\": %f, \"timestamp\": %d}",
                peakLoad.getCpuLoad(), peakLoad.getMemoryLoad(), peakLoad.getTimestamp());
    }


    public String getComplete() throws JSONException {
        List<SystemLoad> allSystemLoads = getTop60ByTimeDesc();
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
    public String getDiscUsageInPercentage(){
        return String.valueOf(getDiskUsagePercentage());
    }

    /**
     * Diese Methode liefert Informationen über den Speicherplatz auf der Festplatte, einschließlich
     * des gesamten Speicherplatzes, des genutzten Speicherplatzes, des freien Speicherplatzes und der
     * prozentualen Nutzung der Festplatte. Die Daten werden im JSON-Format zurückgegeben.
     *
     * @return Eine JSON-String-Repräsentation der Festplattendaten oder "Error" bei Auftreten von Fehlern.
     */
    public String getDiscData(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("total disc space", getTotalDiskSpace());
            obj.put("used disc Space", getUsedDiskSpace());
            obj.put("free disc Space", getUsableDiskSpace());
            obj.put("disc usage in percentage", getDiskUsagePercentage());
            return obj.toString();}
        catch(Exception e){
            return "Error";
        }
    }

}


