package com.analysetool.service;

import com.analysetool.modells.UserRedirectsHourly;
import com.analysetool.repositories.UserRedirectsHourlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRedirectsHourlyService {

    @Autowired
    private UserRedirectsHourlyRepository userRedirectRepo;



}
