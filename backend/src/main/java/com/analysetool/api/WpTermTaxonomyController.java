package com.analysetool.api;

import com.analysetool.modells.WpTermTaxonomy;
import com.analysetool.repositories.WpTermTaxonomyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/wp_term_taxonomy")
public class WpTermTaxonomyController {

    @Autowired
    private WpTermTaxonomyRepository wpTermTaxonomyRepository;

    @GetMapping("/{id}")
    public ResponseEntity<WpTermTaxonomy> getWpTermTaxonomy(@PathVariable Long id) {
        return wpTermTaxonomyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<WpTermTaxonomy> createWpTermTaxonomy(@RequestBody WpTermTaxonomy wpTermTaxonomy) {
        WpTermTaxonomy savedWpTermTaxonomy = wpTermTaxonomyRepository.save(wpTermTaxonomy);
        return ResponseEntity.created(URI.create("/wp_term_taxonomy/" + savedWpTermTaxonomy.getTermTaxonomyId()))
                .body(savedWpTermTaxonomy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WpTermTaxonomy> updateWpTermTaxonomy(@PathVariable Long id, @RequestBody WpTermTaxonomy wpTermTaxonomy) {
        if (!wpTermTaxonomyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        wpTermTaxonomy.setTermTaxonomyId(id);
        WpTermTaxonomy updatedWpTermTaxonomy = wpTermTaxonomyRepository.save(wpTermTaxonomy);
        return ResponseEntity.ok(updatedWpTermTaxonomy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWpTermTaxonomy(@PathVariable Long id) {
        if (!wpTermTaxonomyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        wpTermTaxonomyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
