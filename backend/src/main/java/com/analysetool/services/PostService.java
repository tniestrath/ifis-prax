package com.analysetool.services;

import com.analysetool.modells.wp_term_relationships;
import com.analysetool.repositories.PostRepository;
import com.analysetool.repositories.PostStatsRepository;
import com.analysetool.repositories.WpTermRelationshipsRepository;
import com.analysetool.repositories.WpTermTaxonomyRepository;
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
    private List<Long> getTagsForPost(long postId) {
        List<Long> termTaxonomyIds = termRelRepo.getTaxIdByObject(postId);
        return taxTermRepo.getTermIdByTaxId(termTaxonomyIds);
    }

/*    private Map<Long, Float> getSimilarPosts(long postId, List<Long> tagIdsForPostGiven, float similarityPercentage) {
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
    }*/

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



}
