package com.analysetool.api;

import com.analysetool.modells.FeatureWishes;
import com.analysetool.repositories.FeatureWishesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/feature")
public class FeatureWishController {

    @Autowired
    FeatureWishesRepository featureRepo;

    private final String hardcodedHTML = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Feedback</title><style>h1 { display: flex; height: 80px; width: 100%; text-align: center; } form { display: flex; height: 100%; width: 100%; }</style></head><body><h1>Feedback zum Dashboard: </h1><form name=\"feedback\" action=\"/addWish\" method=\"get\"><input id=\"isNew\" type=\"checkbox\" name=\"isNew\" value=\"true\"><label for=\"isNew\">Soll eine Kachel angelegt werden? (wenn eine bestehende Kachel überarbeitet werden soll bitte leer lassen)</label><input id=\"desc\" type=\"text\" name=\"desc\" placeholder=\"Bitte beschreiben sie ihr gewünschtes Feature kurz\" required><label for=\"team\">Welcher Abteilung gehören sie an: </label><select id=\"team\" name=\"team\" required><option value=\"website\">Website Dev</option><option value=\"admin\">Owner</option><option value=\"editor\">Redaktion</option><option value=\"finance\">Finance</option><option value=\"marketing\">Marketing</option></select><input id=\"email\" type=\"email\" name=\"email\" placeholder=\"Ihre Email adresse für rückfragen\"><input type=\"submit\" value=\"Absenden\"></form><table><tr><th>Team</th><th>Feature</th><th>Fertig?</th></tr>REPLACEREPLACEREPLACE</table></body></html>";
    @Modifying
    @GetMapping("/addWish")
    public boolean addWish(Boolean isNew, String desc, String team, String email) {
        if(featureRepo.getByAllExceptId(email, desc, team, isNew).isEmpty()) {
            FeatureWishes f = new FeatureWishes();
            f.setEmail(email);
            f.setFeature(desc);
            f.setFixed(false);
            f.setNew(isNew);
            f.setTeam(team);
            featureRepo.save(f);
            return true;
        } else {
            return false;
        }
    }

    @Modifying
    @GetMapping("/setFixed")
    public boolean setFixed(long id) {
        if(featureRepo.findById(id).isPresent()) {
            FeatureWishes f = featureRepo.findById(id).get();
            f.setFixed(true);
            featureRepo.save(f);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/feedbackSite")
    public String getAllAndFeedbackOption() throws IOException {
        String html = hardcodedHTML;
        StringBuilder tableContent = new StringBuilder();
        for(FeatureWishes f : featureRepo.findAll()) {
            tableContent.append("<tr>");

            tableContent.append("<td>").append(f.getTeam()).append("</td>");
            tableContent.append("<td>").append(f.getFeature()).append("</td>");
            tableContent.append("<td>").append(f.isFixed()).append("</td>");

            tableContent.append(tableContent.append("</tr>"));
        }
        html = html.replace("REPLACEREPLACEREPLACE", tableContent);
        return html;
    }

    @GetMapping("/testSite")
    public String getTestForSite() throws IOException {
        return hardcodedHTML;
    }

}
