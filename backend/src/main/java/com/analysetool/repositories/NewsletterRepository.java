package com.analysetool.repositories;

import com.analysetool.modells.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    @Query("SELECT n.status FROM Newsletter n WHERE n.id = :id")
    char getStatusById(Long id);

    @Query("SELECT n.status FROM Newsletter n")
    List<Character> getStatusAll();

    @Query("SELECT n.status FROM Newsletter n WHERE n.email = :umail")
    char getStatusByMail(String umail);

    @Query("SELECT n.email FROM Newsletter n WHERE n.status = :ustatus")
    List<String> getMailsByStatus(char ustatus);

    @Query("SELECT n.email, n.status FROM Newsletter n")
    Map<String, Character> getMailAndStatusAll();

    @Query("SELECT n.wp_user_id FROM Newsletter n WHERE n.id=:id")
    long getWpUserIdById(long id);
}
