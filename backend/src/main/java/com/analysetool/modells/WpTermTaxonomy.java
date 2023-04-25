package com.analysetool.modells;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "wp_term_taxonomy")
public class WpTermTaxonomy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_taxonomy_id")
    private Long termTaxonomyId;

    public Long getTermTaxonomyId() {
        return termTaxonomyId;
    }

    public void setTermTaxonomyId(Long termTaxonomyId) {
        this.termTaxonomyId = termTaxonomyId;
    }

    @Override
    public String toString() {
        return "WpTermTaxonomy{" +
                "termTaxonomyId=" + termTaxonomyId +
                ", count=" + count +
                ", description='" + description + '\'' +
                ", parent=" + parent +
                ", taxonomy='" + taxonomy + '\'' +
                ", termId=" + termId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WpTermTaxonomy that)) return false;
        return Objects.equals(getTermTaxonomyId(), that.getTermTaxonomyId()) && Objects.equals(getCount(), that.getCount()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getParent(), that.getParent()) && Objects.equals(getTaxonomy(), that.getTaxonomy()) && Objects.equals(getTermId(), that.getTermId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTermTaxonomyId(), getCount(), getDescription(), getParent(), getTaxonomy(), getTermId());
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    @Column(name = "count")
    private Long count;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "parent")
    private Long parent;

    @Column(name = "taxonomy")
    private String taxonomy;

    @Column(name = "term_id")
    private Long termId;

    // Constructors, getters, and setters
}
