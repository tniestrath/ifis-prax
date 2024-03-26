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

    private final String hardcodedHTML = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Feedback</title><style>body{font-family:Arial,sans-serif;margin:0;padding:0;background-color:#f2f2f2}h1{color:#333}form{background-color:#fff;padding:20px;border-radius:5px;margin-bottom:20px}label{display:inline;margin-bottom:10px;color:#333}input[type=\"text\"],input[type=\"email\"],select{width:100%;padding:10px;margin-bottom:15px;border:1px solid #ccc;border-radius:5px;box-sizing:border-box}input[type=\"checkbox\"]{margin-right:10px;display:inline}input[type=\"submit\"]{background-color:#951D40;color:#fff;padding:10px 20px;border:none;border-radius:5px;cursor:pointer}input[type=\"submit\"]:hover{background-color:#7a1833}table{width:100%;border-collapse:collapse;margin-top:20px}table th,table td{border:1px solid #ccc;padding:8px;text-align:left}table th{background-color:#f2f2f2;color:#333}</style></head><body><h1>Feedback zum Dashboard: </h1><form name=\"feedback\" action=\"/api/feature/addWish\" method=\"get\"><input id=\"isNew\" type=\"checkbox\" name=\"isNew\" value=\"true\"><label for=\"isNew\">Soll eine Kachel angelegt werden? (wenn eine bestehende Kachel überarbeitet werden soll bitte leer lassen)</label><input id=\"desc\" type=\"text\" name=\"desc\" placeholder=\"Bitte beschreiben sie ihr gewünschtes Feature kurz\" required><label for=\"team\">Welcher Abteilung gehören sie an: </label><select id=\"team\" name=\"team\" required><option value=\"website\">Website Dev</option><option value=\"admin\">Owner</option><option value=\"editor\">Redaktion</option><option value=\"finance\">Finance</option><option value=\"marketing\">Marketing</option></select><input id=\"email\" type=\"email\" name=\"email\" placeholder=\"Ihre Email adresse für rückfragen\"><input type=\"submit\" value=\"Absenden\"></form><table><tr>Noch ungelöste, aber schon angefragte Features</tr><tr><th>Team</th><th>Feature</th><th>Fertig?</th></tr>REPLACEREPLACEREPLACE</table><table><tr>Bisher als gelöst angegebene Features</tr>HIERHIERHIER</table></body></html>";
    @Modifying
    @GetMapping("/addWish")
    public boolean addWish(Boolean isNew, String desc, String team, String email) {
        System.out.println("Wunsch ausgefüllt und gestartet");
        try {
            FeatureWishes f = new FeatureWishes();
            f.setEmail(email);
            f.setFeature(desc);
            f.setFixed(false);
            f.setNew(isNew != null);
            f.setTeam(team);
            featureRepo.save(f);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Modifying
    @GetMapping("/flipFixed")
    public boolean setFixed(long id) {
        if(featureRepo.findById(id).isPresent()) {
            FeatureWishes f = featureRepo.findById(id).get();
            f.setFixed(!f.isFixed());
            featureRepo.save(f);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/feedbackSite")
    public String getAllAndFeedbackOption() throws IOException {
        String html = hardcodedHTML;

        html = html.replace("REPLACEREPLACEREPLACE", getHTMLUnfixed());
        html = html.replace("HIERHIERHIER", getHTMLFixed());
        return html;
    }

    private StringBuilder getHTMLUnfixed() {
        StringBuilder tableContentNotFixed = new StringBuilder();
        for(FeatureWishes f : featureRepo.findAllNotFixed()) {
            makeTableRow(tableContentNotFixed, f);
        }
        return tableContentNotFixed;
    }

    private StringBuilder getHTMLFixed() {
        StringBuilder tableContentFixed = new StringBuilder();

        for(FeatureWishes f : featureRepo.findAllFixed()) {
            makeTableRow(tableContentFixed, f);
        }

        return tableContentFixed;
    }

    private void makeTableRow(StringBuilder previousContent, FeatureWishes addRowFrom) {
        String link = "<a href= /api/feature/flipFixed?id=" + addRowFrom.getId() + "target='feet'>" + (addRowFrom.isFixed() ? "Auf ungelöst setzen" : "Auf gelöst setzen") + "</a>";
        previousContent.append("<tr>");

        previousContent.append("<td>").append(addRowFrom.getTeam()).append("</td>");
        previousContent.append("<td>").append(addRowFrom.getFeature()).append("</td>");
        previousContent.append("<td>").append(link).append("</td>");

        previousContent.append("</tr>");
    }

    @GetMapping("/testSite")
    public String getTestForSite() throws IOException {
        return hardcodedHTML;
    }

}
