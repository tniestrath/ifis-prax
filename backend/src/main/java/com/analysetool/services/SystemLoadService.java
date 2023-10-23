package com.analysetool.services;

import com.analysetool.modells.SystemLoad;
import com.analysetool.repositories.SystemLoadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.List;

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
        double networkRecv = networkIF.getBytesRecv();
        double networkSent = networkIF.getBytesSent();

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
        long seventyTwoHoursAgo = System.currentTimeMillis() - (72 * 60 * 60 * 1000); // 72 Stunden in Millisekunden
        systemLoadRepository.deleteByTimestampBefore(seventyTwoHoursAgo);
    }



    public List<SystemLoad> getAllSystemLoads() {
        return systemLoadRepository.findAll();
    }

    public List<SystemLoad> getTop60ByTimeDesc(){
        return systemLoadRepository.getTop60ByOrderByTimestampDesc();
    }
}


