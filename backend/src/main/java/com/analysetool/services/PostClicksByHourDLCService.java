package com.analysetool.services;

import com.analysetool.modells.PostClicksByHourDLC;
import com.analysetool.repositories.PostClicksByHourDLCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PostClicksByHourDLCService {

    @Autowired
    private PostClicksByHourDLCRepository clicksRepo;

    @Transactional
    public void persistAllPostClicksHour(Map<String, PostClicksByHourDLC> postClicksMap) {
        if (!postClicksMap.isEmpty()) {
            clicksRepo.saveAll(postClicksMap.values());
        }
    }
}
