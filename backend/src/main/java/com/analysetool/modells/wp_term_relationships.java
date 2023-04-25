package com.analysetool.modells;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "wp_term_relationships")
public class wp_term_relationships {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "object_id")
    private Long objectId;

    @Column(name = "term_order")
    private Integer termOrder;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Column(name = "term_taxonomy_id")
    private Long termTaxonomyId;

    // getters and setters

    public Integer getTermOrder() {
        return termOrder;
    }

    public void setTermOrder(Integer termOrder) {
        this.termOrder = termOrder;
    }

    public Long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(Long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }
}
