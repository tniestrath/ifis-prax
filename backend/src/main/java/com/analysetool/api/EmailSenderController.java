package com.analysetool.api;

import com.analysetool.util.MailSenderHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RequestMapping(value = {"/emails", "/0wB4P2mly-xaRmeeDOj0_g/emails"})
public class EmailSenderController {

    /**
     * Test the sending of Mails from Spring Mail Server.
     * Has currently been replaced by a Website feature, this method does NOT work.
     * @param email the email to send to.
     * @return a boolean whether the sending worked.
     */
    @GetMapping("/sendMailTo")
    public boolean sendMailTo(String email) {
        MailSenderHelper.getInstance().sendSimpleMessage(email, "hallo ich bins", "eins test");
        return true;
    }



}
