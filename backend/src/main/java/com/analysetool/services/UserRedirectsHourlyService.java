package com.analysetool.services;

import com.analysetool.modells.UserRedirectsHourly;
import com.analysetool.repositories.UserRedirectsHourlyRepository;
import com.analysetool.repositories.universalStatsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    /**
     * Calculates the number of days since user redirects started being tracked.
     *
     * @param userId The ID of the user.
     * @return The number of days since tracking began for the specified user.
     */

    private int getDaysSinceTracking(long userId) {
        if (userRedirectRepo.existsByUserId(userId)) {
            return (int) (userRedirectRepo.getLastUniId() - userRedirectRepo.getFirstUniIdByUserId(userId));
        } else {
            return 0;
        }
    }

    /**
     * Retrieves the total number of redirects across the entire site.
     *
     * @return Total count of redirects for the site.
     */

    public Long getTotalRedirectsOfSite() {
        return userRedirectRepo.getAllRedirectsSummed();
    }

    /**
     * Gets the total number of redirects performed by a specific user.
     *
     * @param userId The ID of the user.
     * @return Total number of redirects for the specified user.
     */

    public Long getAllRedirectsByUserId(Long userId) {
        return userRedirectRepo.getAllRedirectsOfUserIdSummed(userId);
    }

    /**
     * Calculates the average number of redirects per day for a specific user.
     *
     * @param userId The ID of the user.
     * @return Average redirects per day for the user.
     */

    public double getRedirectsPerDay(long userId) {
        int countDays = getDaysSinceTracking(userId);
        long totalRedirects = userRedirectRepo.getAllRedirectsOfUserIdSummed(userId);
        return countDays > 0 ? (double) totalRedirects / countDays : 0;
    }

    /**
     * Determines if there is an upward trend in the number of redirects for a user over the last 7 days.
     *
     * @param userId The ID of the user.
     * @return True if there is an upward trend, false if not, and null if insufficient data.
     */

    public Boolean tendencyUp(long userId) {
        int count = 7;
        long redirects = 0;
        if(getDaysSinceTracking(userId) > 7) {
            for(Integer uni : userRedirectRepo.getLast7Uni()) {
                redirects += userRedirectRepo.findAllByUserIdAndUniId(userId, uni).stream()
                        .mapToLong(UserRedirectsHourly::getRedirects)
                        .sum();
            }
        } else {
            return null;
        }
        Double avg = ((double) redirects / count);
        return avg > getRedirectsPerDay(userId);
    }

    /**
     * Retrieves a map of hourly redirects for a specific user over the last 24 hours, adjusted for a given number of days back.
     *
     * @param userId The ID of the user.
     * @param daysBack The number of days to look back from the current date.
     * @return A map with the hour as the key and the number of redirects as the value.
     */
    public Map<Integer, Long> getUserRedirectsOfLast24HourByUserIdAndDaysBackDistributedByHour(Long userId, Integer daysBack) {
        int latestUniId = uniRepo.getLatestUniStat().getId() - daysBack;
        int previousUniId = latestUniId - 1;

        List<UserRedirectsHourly> combinedRedirects = new ArrayList<>();
        combinedRedirects.addAll(userRedirectRepo.findAllByUserIdAndUniId(userId, previousUniId)); // Data from yesterday
        combinedRedirects.addAll(userRedirectRepo.findAllByUserIdAndUniId(userId, latestUniId));   // Data from today

        Map<Integer, Long> hourlyRedirects = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();

        for (int i = 23; i >= 0; i--) {
            int hour = (currentHour - i + 24) % 24;
            Long redirectCount = combinedRedirects.stream()
                    .filter(redirect -> redirect.getHour() == hour)
                    .map(UserRedirectsHourly::getRedirects)
                    .findFirst()
                    .orElse(0L);
            hourlyRedirects.put(hour, redirectCount);
        }

        return hourlyRedirects;
    }

    /**
     * Generates a map of total redirects for each user across the site.
     *
     * @return A map where each key is a user ID and the corresponding value is the sum of redirects for that user.
     */
    public Map<Long, Long> getTotalRedirectsOfSiteBrokenDownAsMap(){
        List<Object[]> results = userRedirectRepo.getUserIdAndRedirectsSum();
        Map<Long, Long> redirectsMap = new HashMap<>();
        for (Object[] result : results) {
            redirectsMap.put((Long) result[0], (Long) result[1]);
        }
        return redirectsMap;
    }



}
