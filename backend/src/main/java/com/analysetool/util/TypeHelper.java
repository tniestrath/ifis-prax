package com.analysetool.util;

import com.analysetool.modells.Post;
import com.analysetool.modells.WPTerm;
import com.analysetool.modells.WpTermTaxonomy;
import com.analysetool.repositories.WPTermRepository;
import com.analysetool.repositories.WpTermRelationshipsRepository;
import com.analysetool.repositories.WpTermTaxonomyRepository;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class TypeHelper {
    private static TypeHelper INSTANCE;

    @Autowired
    WpTermRelationshipsRepository termRelationRepo;
    @Autowired
    WPTermRepository wpTermRepo;
    @Autowired
    WpTermTaxonomyRepository wpTermTaxonomyRepo;

    private TypeHelper() {

    }

    public static TypeHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TypeHelper();
        }
        return INSTANCE;
    }

public String findTypeForPost(Post post) {
    List<Long> tagIDs = null;
    String type = "default";

    if (termRelationRepo.existsByObjectId(post.getId())) {
        tagIDs = termRelationRepo.getTaxIdByObject(post.getId());
    }
    List<WPTerm> terms = new ArrayList<>();
    if (tagIDs != null) {
        for (long l : tagIDs) {
            if (wpTermRepo.existsById(l)) {
                if (wpTermRepo.findById(l).isPresent()) {
                    terms.add(wpTermRepo.findById(l).get());
                }
            }
        }
    }
    for (WPTerm t : terms) {
        if (wpTermTaxonomyRepo.existsById(t.getId())) {
            if (wpTermTaxonomyRepo.findById(t.getId()).isPresent()) {
                WpTermTaxonomy tt = wpTermTaxonomyRepo.findById(t.getId()).get();
                if (Objects.equals(tt.getTaxonomy(), "category")) {
                    if (wpTermRepo.findById(tt.getTermId()).isPresent() && tt.getTermId() != 1) {
                        type = wpTermRepo.findById(tt.getTermId()).get().getSlug();
                    }
                }
            }
        }
    }

    return type;
}

}
