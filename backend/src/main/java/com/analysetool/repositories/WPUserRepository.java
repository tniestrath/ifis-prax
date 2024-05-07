package com.analysetool.repositories;

import com.analysetool.modells.WPUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u.displayName FROM WPUser u WHERE u.id=:userId")
    String getDisplayNameById(long userId);

    Optional<WPUser> findByDisplayName(String displayname);

    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE um.key='wp_capabilities' AND um.value NOT LIKE '%um_anbieter%' AND um.value NOT LIKE '%admin%'")
    List<WPUser> getAllWithAbo();

    @Query("SELECT u.id FROM WPUser u")
    List<Long> findAllUserIds();

    List<WPUser>findAllByIdIn(List<Long> Ids);
    @Query("SELECT u.displayName from WPUser u WHERE u.id IN :Ids")
    List<String> findAllDisplayNameByIdIn(List<Long> Ids);

    //Set: typeAbo and Name.
    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAbo(String nicename, String typeAbo, Pageable pageable);

    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAbo(String nicename, String typeAbo, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAbo(String nicename, String typeAbo, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAbo(String nicename, String typeAbo, Pageable pageable);


    //Set: typeCompany and Name.

    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='company_category' AND um.value LIKE %:typeCompany% ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingCompany(String nicename, String typeCompany, Pageable pageable);

    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='company_category' AND um.value LIKE %:typeCompany% ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsCompany(String nicename, String typeCompany, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='company_category' AND um.value LIKE %:typeCompany% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsCompany(String nicename, String typeCompany, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId LEFT JOIN WPUserMeta um ON u.id = um.userId WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='company_category' AND um.value LIKE %:typeCompany% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeCompany(String nicename, String typeCompany, Pageable pageable);


    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAboAndCompany(String nicename, String typeAbo, String typeCompany, Pageable pageable);

    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN UserStats s ON u.id = s.userId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAboAndCompany(String nicename, String typeAbo, String typeCompany, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews " +
            "FROM WPUser u " +
            "LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAboAndCompany(String nicename, String typeAbo, String typeCompany, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) " +
            "FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) " +
            "FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE (u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAboAndCompany(String nicename, String typeAbo, String typeCompany, Pageable pageable);


    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAboWithTags(@Param("nicename") String nicename, @Param("typeAbo") String typeAbo, String tag, Pageable pageable);


    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = um.userId WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAboWithTags(String nicename, String typeAbo, String tag, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAboWithTags(String nicename, String typeAbo, String tag, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId LEFT JOIN WPUserMeta um ON u.id = um.userId LEFT JOIN WPUserMeta wum ON u.id = wum.userId WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAboWithTags(String nicename, String typeAbo, String tag, Pageable pageable);


    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN  WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) " +
            "AND um.key='company_category' AND um.value LIKE %:typeCompany% " +
            "ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingCompanyWithTags(String nicename, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN UserStats s ON u.id = s.userId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId  " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) " +
            "AND um.key='company_category' AND um.value LIKE %:typeCompany% " +
            "ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsCompanyWithTags(String nicename, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u " +
            "LEFT JOIN Post p ON u.id = p.authorId " +
            "LEFT JOIN PostStats ps ON p.id = ps.artId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) " +
            "AND um.key='company_category' AND um.value LIKE %:typeCompany% GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsCompanyWithTags(String nicename, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u " +
            "LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%)) " +
            "AND um.key='company_category' AND um.value LIKE %:typeCompany% GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeCompanyWithTags(String nicename, String typeCompany, String tag, Pageable pageable);


    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "LEFT JOIN WPUserMeta rum ON u.id = rum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (rum.key='profile_tags' AND rum.value LIKE %:tag%)) " +
            "AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAboAndCompanyWithTags(String nicename, String typeAbo, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN UserStats s ON u.id = s.userId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "LEFT JOIN WPUserMeta rum ON u.id = rum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename)  AND (rum.key='profile_tags' AND rum.value LIKE %:tag%)) " +
            "AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAboAndCompanyWithTags(String nicename, String typeAbo, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews " +
            "FROM WPUser u " +
            "LEFT JOIN Post p ON u.id = p.authorId " +
            "LEFT JOIN PostStats ps ON p.id = ps.artId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "LEFT JOIN WPUserMeta rum ON u.id = rum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (rum.key='profile_tags' AND rum.value LIKE %:tag%)) " +
            "AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAboAndCompanyWithTags(String nicename, String typeAbo, String typeCompany, String tag, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) " +
            "FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) " +
            "FROM WPUser u " +
            "LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId " +
            "LEFT JOIN WPUserMeta um ON u.id = um.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "LEFT JOIN WPUserMeta rum ON u.id = rum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (rum.key='profile_tags' AND rum.value LIKE %:tag%)) " +
            "AND um.key='wp_capabilities' AND um.value LIKE %:typeAbo% " +
            "AND (wum.key ='company_category' AND wum.value LIKE %:typeCompany%) " +
            "GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAboAndCompanyWithTags(String nicename, String typeAbo, String typeCompany, String tag, Pageable pageable);



    @Query("SELECT u FROM WPUser u WHERE u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAll(String nicename, Pageable pageable);

    @Query("SELECT u FROM WPUser u LEFT JOIN UserStats s ON u.id = s.userId WHERE u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAll(String nicename, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u LEFT JOIN Post p ON u.id = p.authorId LEFT JOIN PostStats ps ON p.id = ps.artId WHERE u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAll(String nicename, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId WHERE u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAll(String nicename, Pageable pageable);



    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%))  ORDER BY u.id DESC")
    List<WPUser> getAllByNicenameContainingAllWithTags(String nicename, String tag, Pageable pageable);

    @Query("SELECT u FROM WPUser u " +
            "LEFT JOIN UserStats s ON u.id = s.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%))  " +
            "ORDER BY s.profileView DESC")
    List<WPUser> getAllNameLikeAndProfileViewsAllWithTags(String nicename, String tag, Pageable pageable);

    @Query("SELECT u, SUM(ps.clicks) AS totalViews FROM WPUser u " +
            "LEFT JOIN Post p ON u.id = p.authorId " +
            "LEFT JOIN PostStats ps ON p.id = ps.artId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%))  GROUP BY u.id ORDER BY totalViews DESC")
    List<WPUser> getAllNameLikeAndContentViewsAllWithTags(String nicename, String tag, Pageable pageable);

    @Query("SELECT u, (SUM(us.views) / (((SELECT MAX(uv.uniId) FROM UserViewsByHourDLC uv)) - (MIN(us.uniId) + 1))) FROM WPUser u " +
            "LEFT JOIN UserViewsByHourDLC us ON u.id = us.userId " +
            "LEFT JOIN WPUserMeta wum ON u.id = wum.userId " +
            "WHERE ((u.nicename LIKE %:nicename% OR CAST(u.id AS STRING)=:nicename) AND (wum.key='profile_tags' AND wum.value LIKE %:tag%))  GROUP BY u.id ORDER BY 2 DESC")
    List<WPUser> getAllNameLikeAndProfileViewsByTimeAllWithTags(String nicename, String tag, Pageable pageable);



    @Query("SELECT u FROM WPUser u LEFT JOIN WPUserMeta um ON u.id=um.userId WHERE um.key='wp_capabilities' AND um.value LIKE CONCAT('%', :typeAbo, '\"%')")
    List<WPUser> getByAboType(String typeAbo);

    @Query("SELECT u.email FROM WPUser u")
    List<String> getAllEmails();

}

