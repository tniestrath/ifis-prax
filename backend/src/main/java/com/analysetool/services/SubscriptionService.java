package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.DashConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    WpTermTaxonomyRepository termTaxRepo;
    @Autowired
    SubscriptionsRepository subRepo;
    @Autowired
    UserSubscriptionsRepository userSubRepo;
    @Autowired
    LoginService loginService;
    @Autowired
    AuthorRelationshipRepository authorRelRepo;
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepo;
    @Autowired
    AuthorRelationshipRepository authorsRepo;
    @Autowired
    WPTermRepository termRepo;
    @Autowired
    PostNotificationsRepository postNotificationsRepo;
    @Autowired
    NotificationMailLogRepository notiMailLogRepo;

    private final DashConfig config;


    private String mailPlaceholder = "<!DOCTYPE html>\n" +
            "<html lang=\"de\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Neue Inhalte Benachrichtigung</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            background-color: #f4f4f4;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "        .container {\n" +
            "            width: 100%;\n" +
            "            max-width: 600px;\n" +
            "            margin: 0 auto;\n" +
            "            background-color: #ffffff;\n" +
            "            padding: 20px;\n" +
            "            border: 1px solid #dddddd;\n" +
            "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        .header {\n" +
            "            text-align: center;\n" +
            "            padding: 10px 0;\n" +
            "        }\n" +
            "        .header h1 {\n" +
            "            margin: 0;\n" +
            "            color: #333333;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "        .content h2 {\n" +
            "            color: #333333;\n" +
            "        }\n" +
            "        .content p {\n" +
            "            color: #666666;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            text-align: center;\n" +
            "            padding: 10px 0;\n" +
            "            color: #999999;\n" +
            "        }\n" +
            "        .button {\n" +
            "            display: inline-block;\n" +
            "            padding: 10px 20px;\n" +
            "            background-color: #007BFF;\n" +
            "            color: #ffffff;\n" +
            "            text-decoration: none;\n" +
            "            border-radius: 5px;\n" +
            "        }\n" +
            "        .button:hover {\n" +
            "            background-color: #0056b3;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1>Neue Inhalte Verfügbar!</h1>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <h2>Hallo [Vorname Nachname],</h2>\n" +
            "            <p>Wir freuen uns, Ihnen mitteilen zu können, dass neue Inhalte auf unserer IT-Sicherheits-Website verfügbar sind. Diese Inhalte sind speziell auf Ihre Interessen abgestimmt und wir sind sicher, dass Sie sie hilfreich und informativ finden werden.</p>\n" +
            "            <p>Um die neuesten Inhalte anzusehen, klicken Sie bitte auf den untenstehenden Button:</p>\n" +
            "            <p><a href=\"https://it-sicherheit.de\" class=\"button\">Jetzt ansehen</a></p>\n" +
            "            <p>Vielen Dank, dass Sie ein geschätztes Mitglied unserer Community sind!</p>\n" +
            "            <p>Mit freundlichen Grüßen,<br>Das IT-Sicherheitsteam</p>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p>&copy; 2024 Ihre IT-Sicherheits-Website. Alle Rechte vorbehalten.</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>\n";

    public SubscriptionService(DashConfig config) {
        this.config = config;
    }

    /**
     * Adds a Subscription to the logged in user, that notifies him on any post releases that fit the given criteria.
     * Not adding a parameter will omit it, and only use the remaining criteria.
     * @param type (Artikel | Blog | News | Video  Podcast) | null
     * @param thema Any tags slug.
     * @param author any authors slug.
     * @param word any String.
     * @param request not manually filled, used to validate userid.
     * @return true if successful, otherwise false.
     */
    public boolean subCustom(String type, String thema, String author, String word, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }


        if(type != null) {
            if (!(type.equals("Artikel") || type.equals("Blog") || type.equals("News") || type.equals("Whitepaper") || type.equals("Video") || type.equals("Podcast"))) {
                if (type.equals("none") || type.isBlank()) type = null;
                else return false;
            }
        }

        Integer tagId = null;
        if(thema != null && !thema.isBlank())  tagId = Math.toIntExact(termTaxRepo.getPostTagBySlug(thema).getTermId());

        Long authorId = 0L;
        if(authorRelRepo.findByAuthorSlugFirst(author).isPresent()) {
            authorId = authorRelRepo.findByAuthorSlugFirst(author).get().getAuthorTerm();
        }
        if(authorId == 0) authorId = null;

        if(word == null || word.isBlank() || word.equals("none")) word = null;

        if(subRepo.findByAll(type, tagId, authorId, word).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).isPresent()) {
                //User already subscribed
                return true;
            } else {
                //Sub exists, sub user
                UserSubscriptions userSub = new UserSubscriptions();
                userSub.setSubId(subRepo.findByAll(type, tagId, authorId, word).get().getId());
                userSub.setUserId(userid);
                userSubRepo.save(userSub);
            }
        } else {
            //Make subscription, add user
            Subscriptions sub = new Subscriptions();
            sub.setAuthor(authorId);
            sub.setTag(tagId);
            sub.setType(type);
            sub.setWord(word);
            subRepo.save(sub);

            UserSubscriptions userSub = new UserSubscriptions();
            userSub.setSubId(sub.getId());
            userSub.setUserId(userid);
            userSubRepo.save(userSub);
        }
        return true;
    }

    public boolean unsubscribe(String type, String thema, String author, String word, HttpServletRequest request) {
        String result = loginService.validateCookie(request);
        int userid;
        try {
            userid = new JSONObject(result).getInt("user_id");
        } catch (JSONException e) {
            return false;
        }


        if(type != null) {
            if (!(type.equals("Artikel") || type.equals("Blog") || type.equals("News") || type.equals("Whitepaper") || type.equals("Video") || type.equals("Podcast"))) {
                if (type.equals("none") || type.isBlank()) type = null;
                else return false;
            }
        }

        Integer tagId = null;
        if(thema != null && !thema.isBlank())  tagId = Math.toIntExact(termTaxRepo.getPostTagBySlug(thema).getTermId());

        Long authorId = 0L;
        if(authorRelRepo.findByAuthorSlugFirst(author).isPresent()) {
            authorId = authorRelRepo.findByAuthorSlugFirst(author).get().getAuthorTerm();
        }
        if(authorId == 0) authorId = null;

        if(word.isBlank() || word.equals("none")) word = null;

        if(subRepo.findByAll(type, tagId, authorId, word).isPresent()) {
            if(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).isPresent()) {
                //User is subscribed, so unsubscribe him.

                userSubRepo.delete(userSubRepo.findByUserIdAndSubId(userid, subRepo.findByAll(type, tagId, authorId, word).get().getId()).get());

                if(userSubRepo.findBySubId(subRepo.findByAll(type, tagId, authorId, word).get().getId()).isEmpty()) {
                    //If no one is subscribed to this, delete it.
                    subRepo.delete(subRepo.findByAll(type, tagId, authorId, word).get());
                }
                return true;
            }
        }
        return true;
    }

    @Scheduled(cron = "0 0 8-18 * * *")
    public void sendNotifications() {
        List<UserSubscriptions> userSubs = userSubRepo.findAll();
        for(Post post : postRepo.findAllUnnotified()) {
            //Find all relevant information about the new post
            String type = postService.getType(post.getId());
            List<Long> tags = postService.getTagsById(post.getId());
            List<Long> authors = authorsRepo.findAuthorsTermIdsByPostId(post.getId());
            String title = post.getTitle();
            List<String> terms = termRepo.getNamesFromList(tags);
            boolean wasSent = false;
            for(UserSubscriptions userSub : userSubs) {
                PostNotifications noti = new PostNotifications();
                if(subRepo.findById(userSub.getSubId()).isPresent()) {
                    Subscriptions sub = subRepo.findById(userSub.getSubId()).get();
                    //Check whether the criteria of any user subscription fit.
                    if((sub.getType() == null || sub.getType().equals(type))
                            && (sub.getTag() == null || tags.contains((long) sub.getTag()))
                            && (sub.getAuthor() == null || authors.contains(sub.getAuthor()))
                            && (sub.getWord() == null || (title.toLowerCase().contains(sub.getWord().toLowerCase()) || terms.contains(sub.getWord())))
                    ) {
                        //Since the criteria fit, notify user
                        noti.setNotificationSent(true);
                        wasSent = true;
                        generateMail(userSub.getUserId());
                    } else {
                        if(!wasSent) noti.setNotificationSent(false);
                    }

                    noti.setPostId(post.getId());
                    noti.setTime(Timestamp.valueOf(LocalDateTime.now()));
                    postNotificationsRepo.save(noti);
                }
            }
        }
        if(!notiMailLogRepo.findAllUnsent().isEmpty()) {
            sendMailsNotifications();
        }

    }

    private void generateMail(long userId) {
        NotificationMailLog noti = new NotificationMailLog();
        noti.setContent(mailPlaceholder);
        noti.setSent(false);
        noti.setUserId(userId);
        notiMailLogRepo.save(noti);
    }


    public boolean sendMailsNotifications() {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(config.getNotificationsend());
            HttpResponse response2 = httpClient.execute(httpPost);
            System.out.println(response2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
