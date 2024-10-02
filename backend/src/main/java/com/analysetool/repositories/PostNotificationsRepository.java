package com.analysetool.repositories;

import com.analysetool.modells.PostNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostNotificationsRepository extends JpaRepository<PostNotifications, Long> {

}
