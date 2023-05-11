package com.analysetool.api;

import com.analysetool.modells.stats;
import com.analysetool.repositories.statsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stats")
public class statsController {
    @Autowired
    private statsRepository statRepository;

    @PostMapping
    public stats createStat(@RequestBody stats stat) {
        return statRepository.save(stat);
    }

    @GetMapping("/{id}")
    public Optional<stats> getStat(@PathVariable Long id) {
        return statRepository.findById(id);
    }

    @GetMapping
    public List<stats> getAllStats() {
        return statRepository.findAll();
    }
 /*
    @PutMapping("/{id}")
    public stats updateStat(@PathVariable Long id, @RequestBody stats stat) {
        Optional<stats> existingStat = statRepository.findById(id);
        if (existingStat.isPresent()) {
            stat.setId(existingStat.get().getId());
            return statRepository.save(stat);
        } else {
            throw new ResourceNotFoundException("Stat not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteStat(@PathVariable Long id) {
        statRepository.deleteById(id);
    }*/
}
