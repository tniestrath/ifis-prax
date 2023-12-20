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

    // benutzerdefinierte Methoden, falls benötigt
    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE u.nicename LIKE %:nicename% AND um.key='wp_capabilities' AND um.value LIKE %:type% ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContaining(String nicename, String type, Pageable pageable);

    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE u.nicename LIKE %:nicename% AND um.key='wp_capabilities' AND um.value LIKE %:type% ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViews(String nicename, String type, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE u.nicename LIKE %:nicename% AND um.key='wp_capabilities' AND um.value LIKE %:type% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViews(String nicename, String type, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE u.nicename LIKE %:nicename% AND um.key='wp_capabilities' AND um.value LIKE %:type% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTime(String nicename, String type, Pageable pageable);


    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE u.nicename LIKE %:nicename% ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAll(String nicename, Pageable pageable);

    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId WHERE u.nicename LIKE %:nicename% ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAll(String nicename, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId WHERE u.nicename LIKE %:nicename% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAll(String nicename, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId WHERE u.nicename LIKE %:nicename% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAll(String nicename, Pageable pageable);

}

