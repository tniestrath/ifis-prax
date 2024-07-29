package com.analysetool.services;

import com.analysetool.modells.wp_term_relationships;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Calculates and returns the average click counts for each post category,
     * sorted by average clicks in descending order.
     *
     * @return a JSON string representing the average click counts for each category
     */
    public String getAverageClicksOfCategoriesRanked() throws JSONException {
        JSONObject result = new JSONObject();
        for(String type : Constants.getInstance().getListOfPostTypesNoEvents()) {
            switch(type) {
                case "blog" -> result.put("Blogs", postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
                case "podcast" -> result.put("Podcasts", postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
                default -> result.put(type, postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) == null ? 0 : postStatRepo.getSumClicksPostsInList(postTypeRepo.getPostsByTypeLong(type)) / postTypeRepo.getPostsByTypeLong(type).size());
            }
        }

        return result.toString();
    }


}
