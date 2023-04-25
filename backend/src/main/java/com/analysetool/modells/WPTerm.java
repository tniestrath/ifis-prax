package com.analysetool.modells;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "wp_terms")
public class WPTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "term_group")
    private Long termGroup;

    // Konstruktoren, Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getTermGroup() {
        return termGroup;
    }

    public void setTermGroup(Long termGroup) {
        this.termGroup = termGroup;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WPTerm wpTerm)) return false;
        return Objects.equals(getId(), wpTerm.getId()) && Objects.equals(getName(), wpTerm.getName()) && Objects.equals(getSlug(), wpTerm.getSlug()) && Objects.equals(getTermGroup(), wpTerm.getTermGroup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSlug(), getTermGroup());
    }

    @Override
    public String toString() {
        return "WPTerm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", termGroup=" + termGroup +
                '}';
    }
}
