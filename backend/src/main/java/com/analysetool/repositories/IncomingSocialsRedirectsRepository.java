package com.analysetool.repositories;

import com.analysetool.modells.IncomingSocialsRedirects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomingSocialsRedirectsRepository  extends JpaRepository<IncomingSocialsRedirects, Long> {

    Optional<IncomingSocialsRedirects> findByUniIdAndHour(int uniId, int hour);

    @Query("SELECT SUM(s.linkedin) FROM IncomingSocialsRedirects s")
    int getSumLinkedin();

    @Query("SELECT SUM(s.twitter) FROM IncomingSocialsRedirects s")
    int getSumTwitter();

    @Query("SELECT SUM(s.facebook) FROM IncomingSocialsRedirects s")
    int getSumFacebook();

    @Query("SELECT SUM(s.youtube) FROM IncomingSocialsRedirects s")
    int getSumYoutube();
}
