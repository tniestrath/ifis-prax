package com.analysetool.services;
import com.analysetool.modells.ContentDownloadsHourly;
import com.analysetool.modells.UserViewsByHourDLC;
import com.analysetool.repositories.ContentDownloadsHourlyRepository;
import com.analysetool.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContentDownloadsHourlyService {

    @Autowired
    private ContentDownloadsHourlyRepository contentDownloadsHourlyRepo;
    @Autowired
    private PostRepository postRepo;

    @Transactional
    public void persistAllContentDownloadsHourly(Map<String, ContentDownloadsHourly> contentDownloadsMap) {
        if (!contentDownloadsMap.isEmpty()) {
            contentDownloadsHourlyRepo.saveAll(contentDownloadsMap.values());
        }
    }
    private int getDaysSinceTracking(long postId) {
        if(contentDownloadsHourlyRepo.existsByPostId(postId)) {
            return (int) (contentDownloadsHourlyRepo.getLastUniId() - contentDownloadsHourlyRepo.getFirstUniIdByPostId(postId));
        } else {
            return 0;
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
    public Long getAllContentDownloadsByPostIds(List<Long> postIds){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostIdIn(postIds);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    public Long getAllContentDownloadsByPostIdsAndUniId(List<Long> postIds, Integer uniId){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostIdInAndUniId(postIds, uniId);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    public double getDownloadsPerDay(long postId) {
        int countDays = getDaysSinceTracking(postId);
        long totalDownloads = 0;
        int lastUniId = 0;
        for(ContentDownloadsHourly c : contentDownloadsHourlyRepo.findAllByPostId(postId)) {
            if(lastUniId != c.getUniId()) {
                lastUniId = c.getUniId();
            }
            totalDownloads= c.getDownloads();
        }
        if(countDays > 0) {
            return (double) totalDownloads / countDays;
        } else {
            return 0;
        }
    }
    public Boolean tendencyUp(long postId) {
        int count = 7;
        int downloads = 0;
        if(getDaysSinceTracking(postId) > 7) {
            for(Integer uni : contentDownloadsHourlyRepo.getLast7Uni()) {
                for(ContentDownloadsHourly c : contentDownloadsHourlyRepo.findAllByPostIdAndUniId(postId, uni)) {
                    downloads += c.getDownloads();
                }
            }
        } else {
            return null;
        }
        Double avg = ((double) downloads / count);
        if(avg > getDownloadsPerDay(postId)) return true;
        if(avg.equals(getDownloadsPerDay(postId))) return null;
        return false;
    }
    public Long getAllDownloadsOfUserContentByUserId(Long userId){
        List<Long> postIdsOfUser = postRepo.findPostIdsByUserId(userId);
        return getAllContentDownloadsByPostIds(postIdsOfUser);
    }
    public String getAllDownloadsOfUserContentBrokenDownByUserId(Long userId){
        Long downloadCount=0L;
        //postId:DownloadCount
        Map<Long,Long> downloadsOfPost= new HashMap<>();
        for(Long postId:postRepo.findPostIdsByUserId(userId)){
            downloadCount=getAllContentDownloadsByPostId(postId);
            if(downloadCount>0){
            downloadsOfPost.put(postId,downloadCount);}
        }
        return downloadsOfPost.toString();
    }


}