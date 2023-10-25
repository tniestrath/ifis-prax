package com.analysetool.api;

import com.analysetool.repositories.UniqueUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/uniqueusers")
public class UniqueUserController {

    @Autowired
    UniqueUserRepository uniqueUserRepo;

    @GetMapping("/getCountGlobal")
    public int getCountGlobal() {
        return uniqueUserRepo.getUserCountGlobal();
    }

    /**
     *
     * @param category "article" | "blog" | "news" | "whitepaper" | "podcast" | "ratgeber" | "global" | "main" | "ueber" | "impressum" | "preisliste" | "partner" | "datenschutz" | "newsletter" | "image" | "agb"
     * @return count of all uniqueusers, that visited the subsite of "category" first.
     */
    @GetMapping("/getCountByFirstCategory")
    public int getCountByFirstCategory(String category) {
        return uniqueUserRepo.getUserCountByFirstCategory(category);
    }

    /**
     *
     * @param category the category you want to check for. Valid values for category are ("article" | "blog" | "news" | "whitepaper" | "podcast" | "ratgeber" | "global" | "main" | "ueber" | "impressum" | "preisliste" | "partner" | "datenschutz" | "newsletter" | "image" | "agb")
     * @return the count of all Users that have visited the subsite of "category" at all
     */
    @GetMapping("/getCountByCategory")
    public int getCountByCategory(String category) {

        switch(category) {
            case "article":
                return uniqueUserRepo.getUserCountArticle();
            case "blog":
                return uniqueUserRepo.getUserCountBlog();
            case "news":
                return uniqueUserRepo.getUserCountNews();
            case "whitepaper":
                return uniqueUserRepo.getUserCountWhitepaper();
            case "podcast":
                return uniqueUserRepo.getUserCountPodcast();
            case "ratgeber":
                return uniqueUserRepo.getUserCountRatgeber();
            case "global":
                return uniqueUserRepo.getUserCountGlobal();
            case "main":
                return uniqueUserRepo.getUserCountMain();
            case "ueber":
                return uniqueUserRepo.getUserCountUeber();
            case "impressum":
                return uniqueUserRepo.getUserCountImpressum();
            case "preisliste":
                return uniqueUserRepo.getUserCountPreisliste();
            case "partner":
                return uniqueUserRepo.getUserCountPartner();
            case "datenschutz":
                return uniqueUserRepo.getUserCountDatenschutz();
            case "newsletter":
                return uniqueUserRepo.getUserCountNewsletter();
            case "image":
                return uniqueUserRepo.getUserCountImage();
            case "agb":
                return uniqueUserRepo.getUserCountAGB();
            default:
                return 0;
        }
    }
}
