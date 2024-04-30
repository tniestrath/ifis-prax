package com.analysetool.services;

import com.analysetool.modells.ForumDiskussionsthemenClicksByHour;
import com.analysetool.repositories.ForumDiskussionsthemenClicksByHourRepository;
import com.analysetool.repositories.WPWPForoForumRepository;
import com.analysetool.repositories.WPWPForoPostsRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ForumService {

    @Autowired
    ForumDiskussionsthemenClicksByHourRepository clicksByHourRepo;
    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    WPWPForoForumRepository forumRepo;
    @Autowired
    WPWPForoPostsRepository forumPostsRepo;

    @Transactional
    public void persistAllForumDiscussionsClicksHour(Map<Long, ForumDiskussionsthemenClicksByHour> forumDiskussionsClicksMap) {
        if (!forumDiskussionsClicksMap.isEmpty()) {
            clicksByHourRepo.saveAll(forumDiskussionsClicksMap.values());
        }
    }

    public Long getForumIdBySlug(String slug){
        return forumRepo.getForumIdBySlug(slug);
    }

}
