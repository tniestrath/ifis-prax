package com.analysetool.services;

import com.analysetool.modells.TemporarySearchStat;
import com.analysetool.repositories.TemporarySearchStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemporarySearchStatService {

    @Autowired
    private TemporarySearchStatRepository repository;

    public void saveSearchStat(TemporarySearchStat searchStat) {
        repository.save(searchStat);
    }

    // Weitere Geschäftslogik
}
