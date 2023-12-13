package com.analysetool.services;
import com.analysetool.modells.UserViewsByHourDLC;
import com.analysetool.repositories.UserViewsByHourDLCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserViewsByHourDLCService {

    @Autowired
    private UserViewsByHourDLCRepository userViewHourDLCRepo;

    @Transactional
    public void persistAllUserViewsHour(Map<String, UserViewsByHourDLC> userViewsMap) {
        if (!userViewsMap.isEmpty()) {
            userViewHourDLCRepo.saveAll(userViewsMap.values());
        }
    }
}