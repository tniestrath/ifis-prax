package com.analysetool.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.net.URI;
import com.analysetool.repositories.WpTermRelationshipsRepository;
import com.analysetool.modells.wp_term_relationships;
@CrossOrigin
@RestController
//@RequestMapping("/wp_term_relationships")
public class WpTermRelationshipsController {

    @Autowired
    private WpTermRelationshipsRepository wpTermRelationshipsRepository;

    @GetMapping("/wp_term_rel/getall")
    public List<wp_term_relationships> getAllWpTermRel(){return wpTermRelationshipsRepository.findAll();}
    @GetMapping("/wp_term_rel/find/{id}")
    public ResponseEntity<wp_term_relationships> findById(@PathVariable Long id) {
        Optional<wp_term_relationships> wpTermRelationships = wpTermRelationshipsRepository.findById(id);
        return wpTermRelationships.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("wp_term_rel/create")
    public ResponseEntity<wp_term_relationships> create(@RequestBody wp_term_relationships wpTermRelationships) {
        wp_term_relationships createdWpTermRelationships = wpTermRelationshipsRepository.save(wpTermRelationships);
        return ResponseEntity.created(URI.create("/wp_term_relationships/" + createdWpTermRelationships.getObjectId())).body(createdWpTermRelationships);
    }

    @PutMapping("/wp_term_rel/update/{id}")
    public ResponseEntity<wp_term_relationships> update(@PathVariable Long id, @RequestBody wp_term_relationships wpTermRelationships) {
        Optional<wp_term_relationships> existingWpTermRelationships = wpTermRelationshipsRepository.findById(id);
        if (existingWpTermRelationships.isPresent()) {
            wpTermRelationships.setObjectId(id);
            wpTermRelationshipsRepository.save(wpTermRelationships);
            return ResponseEntity.ok(wpTermRelationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/wp_term_rel/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wpTermRelationshipsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

