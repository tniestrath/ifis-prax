package com.analysetool.repositories;

import com.analysetool.modells.IPsByPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPsByPostRepository extends JpaRepository<IPsByPost, Long> {

}
