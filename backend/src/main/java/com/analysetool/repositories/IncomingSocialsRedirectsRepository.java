package com.analysetool.repositories;

import com.analysetool.modells.IncomingSocialsRedirects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IncomingSocialsRedirectsRepository  extends JpaRepository<IncomingSocialsRedirects, Long> {

    Optional<IncomingSocialsRedirects> findByUniIdAndHour(int uniId, int hour);

}
