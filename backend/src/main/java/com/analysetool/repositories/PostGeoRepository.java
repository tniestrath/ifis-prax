package com.analysetool.repositories;

import com.analysetool.modells.PostGeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostGeoRepository extends JpaRepository<PostGeo, Long> {

    @Query("SELECT u FROM PostGeo  u WHERE u.post_id=:postId AND u.uniStatId=:uniStatId")
    PostGeo findByPostIdAndUniStatId(long postId, int uniStatId);
}
