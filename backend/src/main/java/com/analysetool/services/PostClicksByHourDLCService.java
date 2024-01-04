package com.analysetool.services;

import com.analysetool.modells.PostClicksByHourDLC;
import com.analysetool.modells.UserViewsByHourDLC;
import com.analysetool.repositories.PostClicksByHourDLCRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostClicksByHourDLCService {

    @Autowired
    private PostClicksByHourDLCRepository clicksRepo;
    @Autowired
    private universalStatsRepository uniRepo;

    @Transactional
    public void persistAllPostClicksHour(Map<String, PostClicksByHourDLC> postClicksMap) {
        if (!postClicksMap.isEmpty()) {
            clicksRepo.saveAll(postClicksMap.values());
        }
    }



    //Hour:Clicks
    public Map<Integer,Long> getPostClicksOfLast24HourByPostIdAndDaysBackDistributedByHour(Long postId, Integer daysback){
        int latestUniId = uniRepo.getLatestUniStat().getId() - daysback;
        int previousUniId = latestUniId - 1;

        List<PostClicksByHourDLC> combinedViews = new ArrayList<>();
        combinedViews.addAll(clicksRepo.findAllByPostIdAndUniId(postId, previousUniId)); // Daten von gestern
        combinedViews.addAll(clicksRepo.findAllByPostIdAndUniId(postId, latestUniId));   // Daten von heute

        Map<Integer, Long> hourlyClicks = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();

        for (int i = 23; i >= 0; i--) {
            int hour = (currentHour - i + 24) % 24;
            Long viewCount = combinedViews.stream()
                    .filter(view -> view.getHour() == hour)
                    .map(PostClicksByHourDLC::getClicks)
                    .findFirst()
                    .orElse(0L);
            hourlyClicks.put(hour, viewCount);
        }

        return hourlyClicks;
    }

    //Index 0 = Summe , Index 1 = Anzahl der tatsächlichen Tage
    public Long[] getSumByDaysbackWithActualDaysBack(List<Long> postIds,int daysBack){
        Pageable pageable = PageRequest.of(0, daysBack);
        Page<Integer> page = uniRepo.getLastIdsByPageable(pageable);

        List<Integer> uniIds = new ArrayList<>(page.getContent());
        List<Integer> availableUniIds = new ArrayList<>(clicksRepo.getAvailableUniIdIn(uniIds));
        Long actualDaysBack= (long)availableUniIds.size();

        Long[] Ergebnis = new Long[]{clicksRepo.sumClicksByPostIdInAndUniIdIn(postIds,availableUniIds),actualDaysBack};

        return Ergebnis;
    }

}
