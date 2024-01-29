package com.analysetool.services;
import com.analysetool.modells.UserViewsByHourDLC;
import com.analysetool.repositories.UserViewsByHourDLCRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserViewsByHourDLCService {

    @Autowired
    private UserViewsByHourDLCRepository userViewHourDLCRepo;
    @Autowired
    private universalStatsRepository uniRepo;

    @Transactional
    public void persistAllUserViewsHour(Map<String, UserViewsByHourDLC> userViewsMap) {
        if (!userViewsMap.isEmpty()) {
            userViewHourDLCRepo.saveAll(userViewsMap.values());
        }
    }

    //Index 0 = Summe , Index 1 = Anzahl der tatsächlichen Tage
    public Long[] getSumByDaysbackWithActualDaysBack(Long userId,int daysBack){
        Pageable pageable = PageRequest.of(0, daysBack);
        Page<Integer> page = uniRepo.getLastIdsByPageable(pageable);

        List<Integer> uniIds = new ArrayList<>(page.getContent());
        List<Integer> availableUniIds = new ArrayList<>(userViewHourDLCRepo.getAvailableUniIdIn(uniIds));
        Long actualDaysBack= (long)availableUniIds.size();

        Long[] Ergebnis = new Long[]{userViewHourDLCRepo.sumViewsByUserIdAndUniIdIn(userId,availableUniIds),actualDaysBack};

        return Ergebnis;
    }

}