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

    /**
     * Calculates the total number of downloads for a specific post.
     *
     * @param postId The ID of the post.
     * @return Total number of downloads for the post.
     */
    public Long getAllContentDownloadsByPostId(Long postId){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostId(postId);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    /**
     * Computes the total downloads for a given post filtered by a specific uniId.
     *
     * @param postId The ID of the post.
     * @param uniId The uniId for filtering downloads.
     * @return Total downloads for the post from the specified uniId.
     */
    public Long getAllContentDownloadsByPostIdAndUniId(Long postId, Integer uniId){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostIdAndUniId(postId, uniId);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    /**
     * Aggregates the total downloads for a list of posts.
     *
     * @param postIds A list of post IDs.
     * @return Combined total downloads for all specified posts.
     */
    public Long getAllContentDownloadsByPostIds(List<Long> postIds){
        List<ContentDownloadsHourly> downloadsList = contentDownloadsHourlyRepo.findAllByPostIdIn(postIds);
        long totalDownloads = downloadsList.stream()
                .mapToLong(ContentDownloadsHourly::getDownloads)
                .sum();
        return totalDownloads;
    }

    /**
     * Computes the total number of downloads for a list of posts, filtered by a specific university ID.
     *
     * @param postIds A list of post IDs to calculate downloads for.
     * @param uniId The uniId to filter the downloads.
     * @return The sum of downloads for all specified posts from the given uniId.
     */
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

    /**
     * Calculates the total downloads for all posts made by a specific user.
     *
     * @param userId The user's ID.
     * @return Total downloads across all posts by the user.
     */
    public Long getAllDownloadsOfUserContentByUserId(Long userId){
        List<Long> postIdsOfUser = postRepo.findPostIdsByUserId(userId);
        return getAllContentDownloadsByPostIds(postIdsOfUser);
    }

    /**
     * Generates a string map of download counts for each post by a given user.
     *
     * @param userId The ID of the user.
     * @return map with postId as key and download count as value.
     */
    public Map<Long, Long> getAllDownloadsOfUserContentBrokenDownByUserIdAsMap(Long userId){
        Long downloadCount=0L;
        //postId:DownloadCount
        Map<Long,Long> downloadsOfPost= new HashMap<>();
        for(Long postId:postRepo.findPostIdsByUserId(userId)){
            downloadCount=getAllContentDownloadsByPostId(postId);
            if(downloadCount>0){
                downloadsOfPost.put(postId,downloadCount);}
        }
        return downloadsOfPost;
    }

    /**
     * Generates a string map of download counts for each post by a given user.
     *
     * @param userId The ID of the user.
     * @return String representation of a map with postId as key and download count as value.
     */
    public String getAllDownloadsOfUserContentBrokenDownByUserIdAsString(Long userId){
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