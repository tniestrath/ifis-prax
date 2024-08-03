package com.analysetool.api;

import com.analysetool.modells.FeatureWishes;
import com.analysetool.repositories.FeatureWishesRepository;
import com.analysetool.services.WishCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/feature", "/0wB4P2mly-xaRmeeDOj0_g/feature"}, method = RequestMethod.GET, produces = "application/json")
public class FeatureWishController {

    @Autowired
    private WishCustomerService wishService;

    /**
     * A hardcoded HTML-File, containing necessary features and CSS.
     */

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
    public boolean addWish(Boolean isNew, String desc, String team, String email) {return wishService.addWish(isNew, desc, team, email);}

    /**
     * Flips the state of a Wish. (true -> false, false -> true).
     * @param id the id of the wish to flip for.
     * @return true if successful - otherwise false.
     */
    @Modifying
    @GetMapping("/flipFixed")
    public boolean setFixed(long id) {return wishService.setFixed(id);}

    /**
     * Builds a Site from all current Feedback and Wishes.
     * @return an HTML-File as a String.
     */
    @GetMapping(value = {"/feedbackSite"}, produces = "text/html")
    public String getAllAndFeedbackOption() {return wishService.getAllAndFeedbackOption();}

}
