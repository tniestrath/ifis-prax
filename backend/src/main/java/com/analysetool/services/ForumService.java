package com.analysetool.services;

import com.analysetool.modells.ForumDiskussionsthemenClicksByHour;
import com.analysetool.modells.ForumTopicsClicksByHour;
import com.analysetool.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ForumService {

    @Autowired
    ForumDiskussionsthemenClicksByHourRepository clicksByHourRepo;
    @Autowired
    ForumTopicsClicksByHourRepository topicClicksByHourRepo;
    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    WPWPForoForumRepository forumRepo;
    @Autowired
    WPWPForoTopicsRepository topicRepo;
    @Autowired
    WPWPForoPostsRepository forumPostsRepo;

    @Transactional
    public void persistAllForumDiscussionsClicksHour(Map<Integer, ForumDiskussionsthemenClicksByHour> forumDiskussionsClicksMap) {
        if (!forumDiskussionsClicksMap.isEmpty()) {
            clicksByHourRepo.saveAll(forumDiskussionsClicksMap.values());
        }
    }

    @Transactional
    public void persistAllForumTopicsClicksHour(Map<Integer, ForumTopicsClicksByHour> forumTopicsClicksMap) {
        if (!forumTopicsClicksMap.isEmpty()) {
            topicClicksByHourRepo.saveAll(forumTopicsClicksMap.values());
        }
    }

    public Integer getForumIdBySlug(String slug){
        return forumRepo.getForumIdBySlug(slug);
    }

    public Integer getTopicIdBySlug(String slug){
        return topicRepo.getTopicIdBySlug(slug);
    }

    public Long getViewsByIdAllTime(Long id){
        return clicksByHourRepo.getClicksAllTimeById(id);
    }

    public Long getViewsByIdToday(Long id){
        Integer uniId = uniRepo.getLatestUniStat().getId();
        return clicksByHourRepo.getClicksOfDay(id,uniId);
    }
}
