package com.analysetool.services;

import com.analysetool.api.UserController;
import com.analysetool.modells.StatMails;
import com.analysetool.modells.WPUser;
import com.analysetool.repositories.StatMailsRepository;
import com.analysetool.repositories.WPUserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class MailGeneratorService {

    @Autowired
    private UserController userController;
    @Autowired
    private StatMailsRepository statMailsRepo;
    @Autowired
    private WPUserRepository userRepo;


    private final String tableBase = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Analyse ihrer Marktplatz Präsens</title></head><body><table id=\"rankings\"><thead><tr><th>Gewählte Themen</th><th>Ihre Platzierung</th><th>Globale Nutzungshäufigkeit</th></tr></thead><tbody>{{TABLEROW}}</tbody></table></body></html>\n";
    private final String tablerowBase = "<tr><td id='rankings-1'>REPLACE1</td><td id='rankings-2'>REPLACE2</td><td id='rankings-3'>REPLACE3</td></tr>";

    @Scheduled(cron = "0 0 0 1 */3 ?")
    public void generateMails() throws JSONException {
        generateMailsPlus();
        generateMailsPremium();
    }

    private void generateMailsPremium() {
        for(WPUser user : userRepo.getByAboType("premium")) {

        }
    }

    private void generateMailsPlus() throws JSONException {
        for(WPUser user : userRepo.getByAboType("plus")) {
            StatMails statMail;
            if(statMailsRepo.findByUserId(user.getId()) == null) {
                statMail = new StatMails();
                statMail.setUserId(Math.toIntExact(user.getId()));
            } else {
                statMail = statMailsRepo.findByUserId(user.getId());
            }

            statMail.setContent(tableBase.replace("{{TABLEROW}}",  makeMailContent("plus", Math.toIntExact(user.getId()))));
            statMail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        }
    }

    public void generateMailSingle(int userId) throws JSONException {
        WPUser user = userRepo.findById((long) userId).get();
        StatMails statMail;
        if(statMailsRepo.findByUserId(user.getId()) == null) {
            statMail = new StatMails();
            statMail.setUserId(Math.toIntExact(user.getId()));
        } else {
            statMail = statMailsRepo.findByUserId(user.getId());
        }

        statMail.setContent(tableBase.replace("{{TABLEROW}}",  makeMailContent("plus", Math.toIntExact(user.getId()))));
        statMail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
    }


    private String makeMailContent(String accType, int userId) throws JSONException {
        StringBuilder content = new StringBuilder();

        JSONArray array = new JSONArray(userController.getSingleUserTagsData(userId, "profile"));

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String row = tablerowBase;
            row = row.replace("REPLACE1", obj.getString("name")).replace("REPLACE2", obj.getString("ranking")).replace("REPLACE3", obj.getString("count"));
            content.append(row);
        }

        return content.toString();
    }

}
