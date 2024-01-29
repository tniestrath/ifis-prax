package com.analysetool.services;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.FinalSearchStatDLC;
import com.analysetool.repositories.FinalSearchStatDLCRepository;
import com.analysetool.repositories.FinalSearchStatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Boolean saveAllBooleanNotBatch(List<FinalSearchStat> stats) {
        Boolean savedAll = false;
        for(FinalSearchStat stat:stats){
          try{
              repository.save(stat);
              savedAll = true;
          }catch(Exception e){
              savedAll=false;
          }
      }
        return savedAll;
    }

    @Transactional
    public Boolean saveAllDLCBooleanNotBatch(List<FinalSearchStatDLC> stats) {
        Boolean savedAll = false;
        for(FinalSearchStatDLC stat:stats){
            try{
                DLCRepo.save(stat);
                savedAll = true;
            }catch(Exception e){
                savedAll=false;
            }
        }
        return savedAll;
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



    @Transactional
    public Boolean saveAllDLCBooleanFromMap(Map<String, List<FinalSearchStatDLC>> statsMap) {
        try {

            List<FinalSearchStatDLC> statsList = new ArrayList<>();

            for (List<FinalSearchStatDLC> dlcList : statsMap.values()) {
                statsList.addAll(dlcList);
            }


            DLCRepo.saveAll(statsList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
