package com.analysetool.services;

import com.analysetool.modells.ForumDiskussionsthemenClicksByHour;
import com.analysetool.modells.ForumSearch;
import com.analysetool.modells.ForumTopicsClicksByHour;
import com.analysetool.modells.WPWPForoForum;
import com.analysetool.repositories.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Autowired
    ForumSearchRepository searchRepo;

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

    public void saveSearchData(ForumSearch forumSearch) {
        searchRepo.save(forumSearch);
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

    public String getRankedDiscussion(int page, int size) throws JSONException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("clicks").descending());
        Page<ForumDiskussionsthemenClicksByHour> forumClicksPage = clicksByHourRepo.findAll(pageable);

        JSONArray jsonArray = new JSONArray();

        for (ForumDiskussionsthemenClicksByHour forumClicks : forumClicksPage) {
            WPWPForoForum forum = forumRepo.findById(forumClicks.getForumId()).orElse(null);
            if (forum != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", forum.getTitle());
                jsonObject.put("clicks", forumClicks.getClicks());
                jsonObject.put("lastPostDate", forum.getLastPostDate().toString());

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray.toString();
    }



}
