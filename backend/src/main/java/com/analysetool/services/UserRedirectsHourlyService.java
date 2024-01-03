package com.analysetool.services;

import com.analysetool.modells.UserRedirectsHourly;
import com.analysetool.repositories.UserRedirectsHourlyRepository;
import com.analysetool.repositories.universalStatsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserRedirectsHourlyService {

    @Autowired
    private UserRedirectsHourlyRepository userRedirectRepo;
    @Autowired
    private universalStatsRepository uniRepo;

    @Transactional
    public void persistAllUserRedirectsHourly(Map<String, UserRedirectsHourly> userRedirectsMap) {
        if (!userRedirectsMap.isEmpty()) {
            userRedirectRepo.saveAll(userRedirectsMap.values());
        }
    }






}
