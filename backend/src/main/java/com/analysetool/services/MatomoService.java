package com.analysetool.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MatomoService {

    private static final String MATOMO_BASE_URL = "https://matomo.internet-sicherheit.de/";

    private final RestTemplate restTemplate;

    public MatomoService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches data from Matomo.
     * @param method the method to request data with.
     * @return the body of matomos response.
     */
    public String getMatomoData( String method) {
        String url = MATOMO_BASE_URL + "?module=API&format=JSON&token_auth=3a485730f2ef726895b30654446d5771"  + "&method=" + method;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}
