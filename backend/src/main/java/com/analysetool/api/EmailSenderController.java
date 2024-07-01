package com.analysetool.api;

import com.analysetool.util.MailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/emails", "/0wB4P2mly-xaRmeeDOj0_g/emails"})
public class EmailSenderController {


    @GetMapping("/sendMailTo")
    public boolean sendMailTo(String email) {
        MailSender.getInstance().sendSimpleMessage(email, "hallo ich bins", "eins test");
        return true;
    }



}
