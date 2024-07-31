package com.analysetool.api;

import com.analysetool.services.RedirectService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/redirect", "/0wB4P2mly-xaRmeeDOj0_g/geo"}, method = RequestMethod.GET, produces = "application/json")
public class RedirectController {

    @Autowired
    RedirectService redirectService;

    @GetMapping("/getSumAllTime")
    public String getSumAllTime() throws JSONException {return redirectService.getSumsOfIncoming();}
}
