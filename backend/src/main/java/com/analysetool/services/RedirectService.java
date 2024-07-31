package com.analysetool.services;

import com.analysetool.repositories.IncomingSocialsRedirectsRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {

    @Autowired
    private IncomingSocialsRedirectsRepository incomingRepo;

    public String getSumsOfIncoming() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("linkedin", incomingRepo.getSumLinkedin());
        json.put("twitter", incomingRepo.getSumTwitter());
        json.put("facebook", incomingRepo.getSumFacebook());
        json.put("youtube", incomingRepo.getSumYoutube());

        return json.toString();
    }
}
