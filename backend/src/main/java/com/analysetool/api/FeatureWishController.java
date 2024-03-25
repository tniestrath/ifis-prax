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
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping("/feature")
public class FeatureWishController {

    @Autowired
    FeatureWishesRepository featureRepo;

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
        String html = Files.readString(Path.of("../../../../resources/feedback.html"));
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
        return Files.readString(Path.of("../../../../resources/feedback.html"));
    }

}
