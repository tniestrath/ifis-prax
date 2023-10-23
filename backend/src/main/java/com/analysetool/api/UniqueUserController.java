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
     * @param category "article" | "blog" | "news" | "whitepaper" | "podcast" | "global" | "mainPage" | "ueber" | "impressum" | "preisliste" | "partner" | "datenschutz" | "newsletter" | "image"
     * @return count of all uniqueusers in said category.
     */
    @GetMapping("/getCountByCategory")
    public int getCountByCategory(String category) {
        return uniqueUserRepo.getUserCountByCategory(category);
    }

}
