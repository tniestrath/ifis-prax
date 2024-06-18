package com.analysetool.api;

import com.analysetool.modells.FeatureWishes;
import com.analysetool.repositories.FeatureWishesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/feature", "/0wB4P2mly-xaRmeeDOj0_g/feature"}, method = RequestMethod.GET, produces = "application/json")
public class FeatureWishController {

    @Autowired
    FeatureWishesRepository featureRepo;

    /**
     * A hardcoded HTML-File, containing necessary features and CSS.
     */
    private final String hardcodedHTML = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Feedback</title><style>body{font-family:Arial,sans-serif;margin:0;padding:0;background-color:#f2f2f2}h1{color:#333}form{background-color:#fff;padding:20px;border-radius:5px;margin-bottom:20px}label{display:inline;margin-bottom:10px;color:#333}input[type=\"text\"],input[type=\"email\"],select{width:100%;padding:10px;margin-bottom:15px;border:1px solid #ccc;border-radius:5px;box-sizing:border-box}input[type=\"checkbox\"]{margin-right:10px;display:inline}input[type=\"submit\"]{background-color:#951D40;color:#fff;padding:10px 20px;border:none;border-radius:5px;cursor:pointer}input[type=\"submit\"]:hover{background-color:#7a1833}table{width:100%;border-collapse:collapse;margin-top:20px}table th,table td{border:1px solid #ccc;padding:8px;text-align:left}table th{background-color:#f2f2f2;color:#333}</style></head><body><h1>Feedback zum Dashboard: </h1><form name=\"feedback\" action=\"/api/feature/addWish\" method=\"get\"><input id=\"isNew\" type=\"checkbox\" name=\"isNew\" value=\"true\"><label for=\"isNew\">Soll eine Kachel angelegt werden? (wenn eine bestehende Kachel überarbeitet werden soll bitte leer lassen)</label><input id=\"desc\" type=\"text\" name=\"desc\" placeholder=\"Bitte beschreiben sie ihr gewünschtes Feature kurz\" required><label for=\"team\">Welcher Abteilung gehören sie an: </label><select id=\"team\" name=\"team\" required><option value=\"website\">Website Dev</option><option value=\"admin\">Owner</option><option value=\"editor\">Redaktion</option><option value=\"finance\">Finance</option><option value=\"marketing\">Marketing</option></select><input id=\"email\" type=\"email\" name=\"email\" placeholder=\"Ihre Email adresse für rückfragen\"><input type=\"submit\" value=\"Absenden\"></form><table><tr>Noch ungelöste, aber schon angefragte Features</tr><tr><th>Team</th><th>Feature</th><th>Fertig?</th></tr>REPLACEREPLACEREPLACE</table><br><br><table><tr>Bisher als gelöst angegebene Features</tr>HIERHIERHIER</table></body></html>";

    /**
     * Adds a new Wish to the database.
     * @param isNew whether the wish includes a new graph.
     * @param desc a description of the feature.
     * @param team the team asking for the feature.
     * @param email an email address for questions regarding the feature.
     * @return true if successful - otherwise false.
     */
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

    /**
     * Flips the state of a Wish. (true -> false, false -> true).
     * @param id the id of the wish to flip for.
     * @return true if successful - otherwise false.
     */
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

    /**
     * Builds a Site from all current Feedback and Wishes.
     * @return an HTML-File as a String.
     */
    @GetMapping(value = {"/feedbackSite"}, produces = "text/html")
    public String getAllAndFeedbackOption() {
        String html = hardcodedHTML;

        html = html.replace("REPLACEREPLACEREPLACE", getHTMLUnfixed());
        html = html.replace("HIERHIERHIER", getHTMLFixed());
        return html;
    }

    /**
     * Builds the innards of an HTML-Table from all unfixed Features-Wishes.
     * @return an HTML tables content as a StringBuilder.
     */
    private StringBuilder getHTMLUnfixed() {
        StringBuilder tableContentNotFixed = new StringBuilder();
        for(FeatureWishes f : featureRepo.findAllNotFixed()) {
            makeTableRow(tableContentNotFixed, f);
        }
        return tableContentNotFixed;
    }

    /**
     * Builds the innards of an HTML-Table from all fixed Features-Wishes.
     * @return an HTML tables content as a StringBuilder.
     */
    private StringBuilder getHTMLFixed() {
        StringBuilder tableContentFixed = new StringBuilder();

        for(FeatureWishes f : featureRepo.findAllFixed()) {
            makeTableRow(tableContentFixed, f);
        }

        return tableContentFixed;
    }

    /**
     * Adds another row to a table's content. Operates in place, previousContent WILL be altered.
     * @param previousContent the previousContents of the table to add into.
     * @param addRowFrom the FeatureWish to add a row from.
     */
    private void makeTableRow(StringBuilder previousContent, FeatureWishes addRowFrom) {
        String link = "<a href= /api/feature/flipFixed?id=" + addRowFrom.getId() + " target='feet'>" + (addRowFrom.isFixed() ? "Auf ungelöst setzen" : "Auf gelöst setzen") + "</a>";
        previousContent.append("<tr>");

        previousContent.append("<td>").append(addRowFrom.getTeam()).append("</td>");
        previousContent.append("<td>").append(addRowFrom.getFeature()).append("</td>");
        previousContent.append("<td>").append(link).append("</td>");

        previousContent.append("</tr>");
    }

}
