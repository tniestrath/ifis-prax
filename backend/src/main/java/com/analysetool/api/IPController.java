package com.analysetool.api;

import com.analysetool.modells.TrackingBlacklist;
import com.analysetool.repositories.TrackingBlacklistRepository;
import com.analysetool.util.DashConfig;
import com.analysetool.util.IPHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/ip")
public class IPController {

    @Autowired
    TrackingBlacklistRepository tbRepo;

    private final DashConfig config;

    @Autowired
    public IPController(DashConfig config) {
        this.config = config;
    }

    /**
     * Fetches a single ips origin.
     * @param ip the ip to fetch for.
     * @return a JSON-String containing detailed geolocation data.<br>
     * Keys are: country, countryISO, district, districtISO, city, cityID.
     * @throws JSONException .
     */
    @GetMapping("/origin")
    public String getOrigin(String ip) throws JSONException {
        JSONObject origin = new JSONObject();
        origin.put("country", IPHelper.getCountryName(ip));
        origin.put("countryISO", IPHelper.getCountryISO(ip));
        origin.put("district", IPHelper.getSubName(ip));
        origin.put("districtISO", IPHelper.getSubISO(ip));
        origin.put("city", IPHelper.getCityName(ip));
        origin.put("cityID", IPHelper.getCityNameId(ip));

        return origin.toString();
    }

    /**
     * Fetches a count of all Unique-Ips found in the access-log.
     * @return a String containing a numeric value.
     */
    @GetMapping("/countUnique")
    public String countIPsInAccessLog() {
        String line;
        HashSet<String> set = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(config.getAccess()))) {
            while ((line = br.readLine()) != null) {
                Pattern pattern = Pattern.compile("^[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()){
                    set.add(matcher.group());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
        return String.valueOf(set.size());
    }

    /**
     * Blocks an IP from tracking, will no longer be analyzed in any category.
     * @param ip the ip to block.
     */
    @PostMapping("/blockIp")
    public void blockIp(String ip) {
        if(tbRepo.findByIp(ip).isEmpty()) {
            TrackingBlacklist tb = new TrackingBlacklist();
            tb.setIp(ip);
            tbRepo.save(tb);
        }
    }


}
