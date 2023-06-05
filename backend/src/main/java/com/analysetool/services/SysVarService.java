package com.analysetool.services;

import com.analysetool.modells.SysVar;
import com.analysetool.repositories.SysVarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SysVarService {

    private final SysVarRepository sysVarRepository;

    @Autowired
    public SysVarService(SysVarRepository sysVarRepository) {
        this.sysVarRepository = sysVarRepository;
    }

    public List<SysVar> getAllSysVars() {
        return sysVarRepository.findAll();
    }

    public Optional<SysVar> getSysVarById(int id) {
        return sysVarRepository.findById(id);
    }

    public SysVar createSysVar(SysVar sysVar) {
        return sysVarRepository.save(sysVar);
    }

    public SysVar updateSysVar(int id, SysVar sysVarDetails) {
        Optional<SysVar> sysVarOptional = sysVarRepository.findById(id);
        if (sysVarOptional.isPresent()) {
            SysVar sysVar = sysVarOptional.get();
            sysVar.setDayInWeek(sysVarDetails.getDayInWeek());
            sysVar.setDayInMonth(sysVarDetails.getDayInMonth());
            sysVar.setDayInYear(sysVarDetails.getDayInYear());
            sysVar.setLastLineCount(sysVarDetails.getLastLineCount());
            sysVar.setLastLine(sysVarDetails.getLastLine());
            sysVar.setDate(sysVarDetails.getDate());
            return sysVarRepository.save(sysVar);
        } else {
            throw new IllegalArgumentException("SysVar not found with ID: " + id);
        }
    }

    public void deleteSysVar(int id) {
        sysVarRepository.deleteById(id);
    }
}
