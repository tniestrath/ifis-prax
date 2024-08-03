package com.analysetool.api;


import com.analysetool.services.IPService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/ip", "/0wB4P2mly-xaRmeeDOj0_g/ip"}, method = RequestMethod.GET, produces = "application/json")
public class IPController {

    @Autowired
    IPService ipService;


    /**
     * Fetches a single ips origin.
     * @param ip the ip to fetch for.
     * @return a JSON-String containing detailed geolocation data.<br>
     * Keys are: country, countryISO, district, districtISO, city, cityID.
     * @throws JSONException .
     */
    @GetMapping("/origin")
    public String getOrigin(String ip) throws JSONException {return ipService.getOrigin(ip);}

    /**
     * Fetches a count of all Unique-Ips found in the access-log.
     * @return a String containing a numeric value.
     */
    @GetMapping("/countUnique")
    public String countIPsInAccessLog() {return ipService.countIPsInAccessLog();}

    /**
     * Blocks an IP from tracking, will no longer be analyzed in any category.
     * @param ip the ip to block.
     */
    @PostMapping("/blockIp")
    public void blockIp(String ip) {ipService.blockIp(ip);}

}
