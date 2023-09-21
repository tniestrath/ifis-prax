package com.analysetool.services;

import com.analysetool.modells.SystemLoad;
import com.analysetool.repositories.SystemLoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.List;

@Service
public class SystemLoadService {

    private final SystemLoadRepository systemLoadRepository;
    private final SystemInfo systemInfo = new SystemInfo();

    @Autowired
    public SystemLoadService(SystemLoadRepository systemLoadRepository) {
        this.systemLoadRepository = systemLoadRepository;
    }

    @Scheduled(fixedRate = 60000) // 1 Minute
    public void recordSystemLoad() {
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        long[] prevTicks = processor.getSystemCpuLoadTicks();

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

        SystemLoad systemLoad = new SystemLoad();
        systemLoad.setCpuLoad(cpuLoad);
        systemLoad.setMemoryLoad(memoryLoad);
        systemLoad.setTimestamp(System.currentTimeMillis());

        systemLoadRepository.save(systemLoad);
    }



    public List<SystemLoad> getAllSystemLoads() {
        return systemLoadRepository.findAll();
    }

}


