package com.analysetool.api;

import com.analysetool.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private IPsByPostRepository iPsByPostRepository;

    @Autowired
    private IPsByUserRepository iPsByUserRepository;

    @Autowired
    private ClicksByCountryRepository clicksByCountryRepo;

    @Autowired
    private ClicksByBundeslandRepository clicksByBundeslandRepo;

    @Autowired
    private PostGeoRepository postGeoRepo;

    @Autowired
    private UserGeoRepository userGeoRepo;






}
