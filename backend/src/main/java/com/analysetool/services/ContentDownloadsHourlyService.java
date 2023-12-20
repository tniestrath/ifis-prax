package com.analysetool.services;
import com.analysetool.modells.ContentDownloadsHourly;
import com.analysetool.repositories.ContentDownloadsHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ContentDownloadsHourlyService {

    @Autowired
    private ContentDownloadsHourlyRepository contentDownloadsHourlyRepo;

    @Transactional
    public void persistAllContentDownloadsHourly(Map<String, ContentDownloadsHourly> contentDownloadsMap) {
        if (!contentDownloadsMap.isEmpty()) {
            contentDownloadsHourlyRepo.saveAll(contentDownloadsMap.values());
        }
    }

    public Long getAllContentDownloadsByPostId(Long postId){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostId(postId);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    public Long getAllContentDownloadsByPostIdAndUniId(Long postId, Integer uniId){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostIdAndUniId(postId, uniId);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }


}