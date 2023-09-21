package com.analysetool.modells;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "system_load")
public class SystemLoad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cpu_load")
    private double cpuLoad;
    @Column(name = "timestamp")
    private long timestamp;
    @Column(name = "memory_load")
    private double memoryLoad;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getMemoryLoad() {
        return memoryLoad;
    }

    public void setMemoryLoad(double memoryLoad) {
        this.memoryLoad = memoryLoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemLoad that)) return false;
        return Double.compare(that.getCpuLoad(), getCpuLoad()) == 0 && getTimestamp() == that.getTimestamp() && Double.compare(that.getMemoryLoad(), getMemoryLoad()) == 0 && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCpuLoad(), getTimestamp(), getMemoryLoad());
    }

}

