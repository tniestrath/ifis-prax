package com.analysetool.services;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.repositories.FinalSearchStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinalSearchStatService {

    @Autowired
    private FinalSearchStatRepository repository;

    public void saveAll(List<FinalSearchStat> stats) {
        repository.saveAll(stats);
    }

    public Boolean saveAllBoolean(List<FinalSearchStat> stats) {
        try{
            repository.saveAll(stats);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    // Weitere Geschäftslogik
}
