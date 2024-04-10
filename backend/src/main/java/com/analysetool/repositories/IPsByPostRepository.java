package com.analysetool.repositories;

import com.analysetool.modells.IPsByPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IPsByPostRepository extends JpaRepository<IPsByPost, Long> {

    @Query("SELECT u FROM IPsByPost u WHERE u.post_id=:id")
    IPsByPost getByID(long id);

}
