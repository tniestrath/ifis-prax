package com.analysetool.repositories;

import com.analysetool.modells.TagCatStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagCatStatRepository extends JpaRepository<TagCatStat, Long> {

    @Query("SELECT t FROM TagCatStat t WHERE t.tagId=:tagId AND t.uniId=:uniId AND t.hour=:hour")
    TagCatStat getTagCatStatByTagIdAndTime(int tagId, int uniId, int hour);

    @Query("SELECT SUM(t.views) FROM TagCatStat t WHERE t.tagId=:tagId")
    int getSumViewsByTag(int tagId);

}
