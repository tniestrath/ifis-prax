package com.analysetool.services;

import com.analysetool.modells.wp_term_relationships;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.analysetool.util.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    WpTermRelationshipsRepository termRelRepo;
    @Autowired
    WpTermTaxonomyRepository taxTermRepo;
    @Autowired
    PostRepository postRepo;
    @Autowired
    PostStatsRepository postStatRepo;
    @Autowired
    PostTypeRepository postTypeRepo;

    private List<Long> getTagsForPost(long postId) {
        List<Long> termTaxonomyIds = termRelRepo.getTaxIdByObject(postId);
        return taxTermRepo.getTermIdByTaxId(termTaxonomyIds);
    }


    public Map<Long, Float> getSimilarPosts(long postId, float similarityPercentage) {
        // Retrieve tags for the given post
        List<Long> tagIdsForPostGiven = getTagsForPost(postId);

        Map<Long, Float> postAndSimilarityMap = new HashMap<>();
        List<wp_term_relationships> allPostsRelationships = termRelRepo.findAll();

        for (wp_term_relationships otherPostRel : allPostsRelationships) {
            Long otherPostId = otherPostRel.getObjectId();
            if (otherPostId.equals(postId)) continue;  // Ignore the given post

            List<Long> tagIdsForOtherPost = getTagsForPost(otherPostId);
            float currentSimilarityPercentage = calculateTagSimilarity(tagIdsForPostGiven, tagIdsForOtherPost);

            if (currentSimilarityPercentage >= similarityPercentage) {
                postAndSimilarityMap.put(otherPostId, currentSimilarityPercentage);
            }
        }
        return postAndSimilarityMap;
    }

    private float calculateTagSimilarity(List<Long> tagsOfPostOne, List<Long> tagsOfPostTwo) {
        int commonTagsCount = (int) tagsOfPostOne.stream().filter(tagsOfPostTwo::contains).count();
        return (commonTagsCount * 1.0f / tagsOfPostOne.size()) * 100;
    }

    public String getAverageClicksOfCategoriesaa(){
        JSONObject obj = new JSONObject();
        List<Integer> artikelIds = postTypeRepo.getPostsByType("artikel");
        List<Long> artikelClicks = new ArrayList<>();
        for (Integer c : artikelIds) {
            Long id = Integer.toUnsignedLong(c);
            postStatRepo.getSumClicksLong(c);
        }
        double meanArtikel = MathHelper.getMeanLong(artikelClicks);
        return obj.toString();
    }

    /**
     * Calculates and returns the average click counts for each post category,
     * sorted by average clicks in descending order.
     *
     * @return a JSON string representing the average click counts for each category
     */
    public String getAverageClicksOfCategoriesRanked() throws JSONException {

        Map<String, Double> meanClicksMap = new HashMap<>();

        for (String category : Constants.getInstance().getListOfPostTypes()) {
            List<Integer> postIds = postTypeRepo.getPostsByType(category);
            List<Long> postClicks = new ArrayList<>();

            for (Integer postId : postIds) {
                Long clicks = postStatRepo.getSumClicksLong(postId);
                if (clicks != null) {
                    postClicks.add(clicks);
                }
            }

            double meanClicks = postClicks.isEmpty() ? 0 : MathHelper.getMeanLong(postClicks);
            meanClicksMap.put(category, meanClicks);
        }

        List<Map.Entry<String, Double>> sortedMeanClicks = new ArrayList<>(meanClicksMap.entrySet());
        sortedMeanClicks.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        JSONObject result = new JSONObject();
        for (Map.Entry<String, Double> entry : sortedMeanClicks) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result.toString();
    }


}
