package com.analysetool.services;

import com.analysetool.repositories.SocialsImpressionsRepository;
import com.analysetool.repositories.universalStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialsImpressionsService {
    @Autowired
    private SocialsImpressionsRepository socialsImpressionsRepo;
    @Autowired
    private universalStatsRepository uniRepo;
}
