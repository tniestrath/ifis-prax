package com.analysetool.services;

import com.analysetool.modells.TemporarySearchStat;
import com.analysetool.repositories.TemporarySearchStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemporarySearchStatService {

    @Autowired
    private TemporarySearchStatRepository repository;

    public List<TemporarySearchStat> getAllSearchStat() {
       return repository.findAll();
    }
    public void saveSearchStat(TemporarySearchStat searchStat) {
        repository.save(searchStat);
    }

    public void deleteSearchStat(TemporarySearchStat searchStat) {
        repository.delete(searchStat);
    }

    public void deleteAllSearchStatIn(List<TemporarySearchStat searchStat>) {
        repository.deleteAll(searchStat);
    }

  
}
