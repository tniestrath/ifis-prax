package com.analysetool.services;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.FinalSearchStatDLC;
import com.analysetool.repositories.FinalSearchStatDLCRepository;
import com.analysetool.repositories.FinalSearchStatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinalSearchStatService {

    @Autowired
    private FinalSearchStatRepository repository;
    @Autowired
    private FinalSearchStatDLCRepository DLCRepo;

    public void saveAll(List<FinalSearchStat> stats) {
        repository.saveAll(stats);
    }

    @Transactional
    public Boolean saveAllBoolean(List<FinalSearchStat> stats) {
        try{
            repository.saveAll(stats);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Transactional
    public Boolean saveAllDLCBoolean(List<FinalSearchStatDLC> stats) {
        try{
            DLCRepo.saveAll(stats);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    // Weitere Geschäftslogik
}
