package com.analysetool.api;

import com.analysetool.util.DashConfig;
import com.analysetool.util.IPHelper;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/ip")
public class IPController {

    @GetMapping("/origin")
    public String getOrigin(String ip){
        return IPHelper.getCountryName(ip)
         + " : " + IPHelper.getSubName(ip)
         +" : " + IPHelper.getCityName(ip);
    }

    @GetMapping("/countUnique")
    public String countIPsInAccessLog() {
        DashConfig config = new DashConfig();
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
}
