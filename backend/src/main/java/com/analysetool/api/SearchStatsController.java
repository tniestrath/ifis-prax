package com.analysetool.api;

import com.analysetool.modells.SearchStats;
import com.analysetool.repositories.SearchStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search-stats")
public class SearchStatsController {

    @Autowired
    private SearchStatsRepository searchStatsRepository;

    /*@Autowired
    public SearchStatsController(SearchStatsRepository searchStatsRepository) {
        this.searchStatsRepository = searchStatsRepository;
    }*/

    @GetMapping("/getAll")
    public List<SearchStats> getAllSearchStats() {
        return searchStatsRepository.findAll();
    }


}

