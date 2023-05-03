package com.analysetool.api;

import com.analysetool.modells.WPTerm;
import com.analysetool.repositories.WPTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/terms")
public class WPTermController {

    @Autowired
    private WPTermRepository termRepository;

    @GetMapping("terms/{id}")
    public ResponseEntity<WPTerm> getTermById(@PathVariable Long id) {
        Optional<WPTerm> term = termRepository.findById(id);
        if (term.isPresent()) {
            return ResponseEntity.ok(term.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("terms/getall")
    List<WPTerm> getall(){return termRepository.findAll();}
    // weitere REST-Endpunkte, falls benötigt

}

