package com.analysetool.repositories;

import com.analysetool.modells.WPUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WPUserRepository extends JpaRepository<WPUser, Long> {

    Optional<WPUser> findByLogin(String login);
    Optional<WPUser> findByEmail(String email);

    Optional<WPUser> findByNicename(String nicename);

    Optional<WPUser> findByActivationKey(String ActivationKey);

    boolean existsByActivationKey(String ActivationKey);

    List<WPUser> getAllByNicenameContaining(String nicename, Pageable pageable);

    // benutzerdefinierte Methoden, falls benötigt

    @Query("SELECT u FROM WPUser u JOIN UserStats s WHERE u.nicename LIKE %:nicename% ORDER BY s.profileView")
    List<WPUser> getAllNameLikeAndProfileViews(String nicename, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId WHERE u.nicename LIKE %:nicename% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViews(String nicename, Pageable pageable);

}

