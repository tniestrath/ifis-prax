package com.analysetool.repositories;

import com.analysetool.modells.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}

