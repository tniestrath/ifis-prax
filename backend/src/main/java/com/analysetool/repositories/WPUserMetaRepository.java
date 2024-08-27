package com.analysetool.repositories;

import com.analysetool.modells.WPUserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WPUserMetaRepository extends JpaRepository<WPUserMeta, Long> {

    boolean existsByUserId(Long user_id);

    WPUserMeta findByUserId(Long user_id);

    @Query("select p.value from WPUserMeta p where p.userId = :id AND p.key = 'wp_capabilities'")
    String getWPUserMetaValueByUserId(Long id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key ='wp_capabilities'")
    List<String> getWpCapabilities();

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='profile_photo' AND p.userId=:user_id")
    Optional<String> getProfilePath(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='cover_photo' AND p.userId=:user_id")
    Optional<String> getCoverPath(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='description' AND p.userId=:user_id")
    Optional<String> getDescription(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='company_slogan' AND p.userId=:user_id")
    Optional<String> getSlogan(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='name_ansprechperson' AND p.userId=:user_id")
    Optional<String> getPersonIntern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='email_ansprechperson' AND p.userId=:user_id")
    Optional<String> getMailIntern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='telefon_ansprechperson' AND p.userId=:user_id")
    Optional<String> getTelIntern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='user_url' AND p.userId=:user_id")
    Optional<String> getURLExtern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='name_oeffentlich_person' AND p.userId=:user_id")
    Optional<String> getNameExtern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='secondary_user_email' AND p.userId=:user_id")
    Optional<String> getSecondaryMail(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='telefon_unternehmen' AND p.userId=:user_id")
    Optional<String> getTelExtern(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='adresse_strasse' AND p.userId=:user_id")
    Optional<String> getAdresseStreet(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='adresse_plz' AND p.userId=:user_id")
    Optional<String> getAdressePLZ(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='adresse_ort' AND p.userId=:user_id")
    Optional<String> getAdresseOrt(Long user_id);


    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='profile_tags_basis' AND p.userId=:user_id")
    Optional<String> getTagsBasis(Long user_id);
    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='profile_tags_basis_plus' AND p.userId=:user_id")
    Optional<String> getTagsBasisPlus(Long user_id);
    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='profile_tags_plus' AND p.userId=:user_id")
    Optional<String> getTagsPlus(Long user_id);
    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='profile_tags_premium' AND p.userId=:user_id")
    Optional<String> getTagsPremium(Long user_id);

    //Counts of Users with a specific tag see aggregate in UserController

    @Query("SELECT count(DISTINCT p.userId) FROM WPUserMeta p WHERE p.key='profile_tags_basis' AND p.value LIKE %:tag%")
    Integer countUsersByTagBasis(String tag);
    @Query("SELECT count(DISTINCT p.userId) FROM WPUserMeta p WHERE p.key='profile_tags_basis_plus' AND p.value LIKE %:tag%")
    Integer countUsersByTagBasisPlus(String tag);
    @Query("SELECT count(DISTINCT p.userId) FROM WPUserMeta p WHERE p.key='profile_tags_plus' AND p.value LIKE %:tag%")
    Integer countUsersByTagPlus(String tag);
    @Query("SELECT count(DISTINCT p.userId) FROM WPUserMeta p WHERE p.key='profile_tags_premium' AND p.value LIKE %:tag%")
    Integer countUsersByTagPremium(String tag);

    //UserIds for Users with a specific tag selected, see aggregate in UserController
    @Query("SELECT DISTINCT p.userId FROM WPUserMeta p WHERE p.key='profile_tags_basis' AND p.value LIKE %:tag%")
    List<Long> getUserIdsByTagBasis(String tag);
    @Query("SELECT DISTINCT p.userId FROM WPUserMeta p WHERE p.key='profile_tags_basis_plus' AND p.value LIKE %:tag%")
    List<Long> getUserIdsByTagBasisPlus(String tag);
    @Query("SELECT DISTINCT p.userId FROM WPUserMeta p WHERE p.key='profile_tags_plus' AND p.value LIKE %:tag%")
    List<Long> getUserIdsByTagPlus(String tag);
    @Query("SELECT DISTINCT p.userId FROM WPUserMeta p WHERE p.key='profile_tags_premium' AND p.value LIKE %:tag%")
    List<Long> getUserIdsByTagPremium(String tag);


    //List of Tag-Rows for all Users in a given list and the type.
    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'profile_tags_basis' AND u.userId IN :list")
    List<String> getAllUserTagRowsInListBasis(List<Long> list);
    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'profile_tags_basis_plus' AND u.userId IN :list")
    List<String> getAllUserTagRowsInListBasisPlus(List<Long> list);
    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'profile_tags_plus' AND u.userId IN :list")
    List<String> getAllUserTagRowsInListPlus(List<Long> list);
    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'profile_tags_premium' AND u.userId IN :list")
    List<String> getAllUserTagRowsInListPremium(List<Long> list);


    //UserIds for Users with Tags see aggregate in UserController

    @Query("SELECT DISTINCT u.userId FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_basis' AND (wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_basis\"%')")
    List<Long> getAllUserIdsWithTagsBasis();
    @Query("SELECT DISTINCT u.userId FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_basis_plus' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_basis-plus\"%'")
    List<Long> getAllUserIdsWithTagsBasisPlus();
    @Query("SELECT DISTINCT u.userId FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_plus' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_plus\"%'")
    List<Long> getAllUserIdsWithTagsPlus();
    @Query("SELECT DISTINCT u.userId FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_premium' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_premium\"%'")
    List<Long> getAllUserIdsWithTagsPremium();


    //Counts of Users with Tags see aggregate in UserController

    @Query("SELECT count(DISTINCT u.userId) FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_basis' AND (wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_basis\"%')")
    Integer getTotalCountOfUsersWithTagBasis();
    @Query("SELECT COUNT(DISTINCT u.userId) FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_basis_plus' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_basis-plus\"%'")
    Integer getTotalCountOfUsersWithTagBasisPlus();
    @Query("SELECT COUNT(DISTINCT u.userId) FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_plus' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_plus\"%'")
    Integer getTotalCountOfUsersWithTagPlus();
    @Query("SELECT COUNT(DISTINCT u.userId) FROM WPUserMeta u LEFT JOIN WPUserMeta wum ON u.userId = wum.userId WHERE u.key = 'profile_tags_premium' AND wum.key = 'wp_capabilities' AND wum.value LIKE '%\"um_premium\"%'")
    Integer getTotalCountOfUsersWithTagPremium();







    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_1' AND p.userId=:user_id")
    Optional<String> getSolutionHead1(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_2' AND p.userId=:user_id")
    Optional<String> getSolutionHead2(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_3' AND p.userId=:user_id")
    Optional<String> getSolutionHead3(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_4' AND p.userId=:user_id")
    Optional<String> getSolutionHead4(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_5' AND p.userId=:user_id")
    Optional<String> getSolutionHead5(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_6' AND p.userId=:user_id")
    Optional<String> getSolutionHead6(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_7' AND p.userId=:user_id")
    Optional<String> getSolutionHead7(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_8' AND p.userId=:user_id")
    Optional<String> getSolutionHead8(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_9' AND p.userId=:user_id")
    Optional<String> getSolutionHead9(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_10' AND p.userId=:user_id")
    Optional<String> getSolutionHead10(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_11' AND p.userId=:user_id")
    Optional<String> getSolutionHead11(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='solution_head_12' AND p.userId=:user_id")
    Optional<String> getSolutionHead12(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='company_employees' AND p.userId=:user_id")
    Optional<String> getCompanyEmployees(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='company_category' AND p.userId=:user_id")
    Optional<String> getCompanyCategory(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='company_manager' AND p.userId=:user_id")
    Optional<String> getManager(Long user_id);

    @Query("SELECT p.value FROM WPUserMeta p WHERE p.key='service_company' AND p.userId=:user_id")
    Optional<String> getService(Long user_id);

    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'security_logo_eu' AND u.userId=:userId")
    Optional<String> getTeleEU(long userId);

    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'security_logo' AND u.userId=:userId")
    Optional<String> getTeleDE(long userId);

    @Query("SELECT u.userId FROM WPUserMeta u WHERE (u.key = 'user_url' AND u.value =:url) OR u.key = '_wpf_member_obj' AND u.value LIKE %:url%")
    List<Long> getUserByURL(String url);

    @Query("SELECT u.userId FROM WPUserMeta u WHERE u.key = 'um_user_profile_url_slug_user_login' AND u.value=:subpageURL")
    Long getUserByLinkToSubpage(String subpageURL);

    @Query("SELECT u.value FROM WPUserMeta u WHERE u.key = 'um_user_profile_url_slug_user_login' AND u.userId=:userid")
    String getUserLinkToSubpage(long userId);

}

