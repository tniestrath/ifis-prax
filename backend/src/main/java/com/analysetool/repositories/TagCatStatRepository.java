package com.analysetool.repositories;

import com.analysetool.modells.TagCatStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagCatStatRepository extends JpaRepository<TagCatStat, Long> {

    @Query("SELECT t FROM TagCatStat t WHERE t.tagId=:tagId")
    TagCatStat getTagCatStatByTagId(int tagId);

}
