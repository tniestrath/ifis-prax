package com.analysetool.services;

import com.analysetool.modells.FinalSearchStat;
import com.analysetool.modells.FinalSearchStatDLC;
import com.analysetool.repositories.FinalSearchStatDLCRepository;
import com.analysetool.repositories.FinalSearchStatRepository;
import com.analysetool.repositories.universalStatsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinalSearchStatService {

    @Autowired
    private FinalSearchStatRepository repository;
    @Autowired
    private FinalSearchStatDLCRepository DLCRepo;
    @Autowired
    private universalStatsRepository uniRepo;
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

    public List<FinalSearchStat> getSearchStatsByPostId(Long postId){
        List<Long> FinalSearchStatIds=DLCRepo.getFinalSearchStatIdsByPostId(postId);
        List<FinalSearchStat> stats = repository.findAllById(FinalSearchStatIds);

        return stats;
    }

    public List<FinalSearchStat> getSearchStatsByUserId(Long userId){
        List<Long> FinalSearchStatIds=DLCRepo.getFinalSearchStatIdsByUserId(userId);
        List<FinalSearchStat> stats = repository.findAllById(FinalSearchStatIds);

        return stats;
    }

    public Map<FinalSearchStat,List<FinalSearchStatDLC>> getSearchStatsByCity(String city){
        Map<FinalSearchStat,List<FinalSearchStatDLC>> response = new HashMap<>();
        List<FinalSearchStat> allSearchesOfCity= repository.findAllByCity(city);
        List<FinalSearchStatDLC> allSearchSuccessesOfSearch = new ArrayList<>();
        for(FinalSearchStat stat:allSearchesOfCity){
            allSearchSuccessesOfSearch = DLCRepo.findAllByFinalSearchId(stat.getId());
            response.put(stat,allSearchSuccessesOfSearch);
        }
        return response;
    }
    public String toStringList(List<FinalSearchStat>stats){
        String response = "";

        for(FinalSearchStat stat:stats){
            response=response+toString(stat);
        }

        return response;
    }

    public String toString(FinalSearchStat stat){
        int uniId = stat.getUniId();
        int newestUniId = uniRepo.getLatestUniStat().getId();
        int daysBack= newestUniId-uniId;
        LocalDate date = LocalDate.now().minusDays(daysBack);
        return stat.toStringAlt(date);
    }

}
