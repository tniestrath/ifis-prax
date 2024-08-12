package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import com.analysetool.util.DashConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private StatMailsRepository statMailsRepo;
    @Autowired
    private WPUserRepository userRepo;
    @Autowired
    private PostService postService;
    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private WPUserMetaRepository wpUserMetaRepository;
    @Autowired
    private UserViewsByHourDLCRepository userViewsRepo;
    @Autowired
    private UserRedirectsHourlyRepository userRedirectsRepo;
    @Autowired
    private RankingGroupContentRepository rankingGroupContentRepo;
    @Autowired
    private RankingGroupProfileRepository rankingGroupProfileRepo;
    @Autowired
    private RankingTotalContentRepository rankingTotalContentRepo;
    @Autowired
    private RankingTotalProfileRepository rankingTotalProfileRepo;
    @Autowired
    private PostClicksByHourDLCRepository postClicksRepo;
    @Autowired
    private NewsletterRepository newsletterRepo;
    @Autowired
    private universalStatsRepository uniRepo;
    @Autowired
    private PostStatsRepository statRepository;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private EventsRepository eventsRepo;
    @Autowired
    private EventsService eventsService;
    @Autowired
    private MembershipBufferRepository memberRepo;

    private final DashConfig config;

    private final String imageProfile = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAIASURBVFiFvdY/a5RBEMfxz5MLRIwRFRtRAioIgn8CsbKwM2KnFhEbCytfhJ2IlWCjIIgWCmqrBHwBFiKEWFgoGBSxSKGFaKLBxLG4S1juLnf73D2XgYFnH57Z33efnZndIiKUsaIoTuIiJnAc3zCHN7gbEYulJoyILMcW3MIqYgOfx6ncOSMiDwAjmO0gnPo/XK4a4Gam+Jr/wHglADiBlZIAgZc5AEMZaXIOtcyUSm2qKIqxbh/lAEz0IA6FepX0DXC0R4Cs2ByAhT4AusbmAMz1AdA1NgfgdY/iCxHxuetXGWVYU2+zZctwuspGdBi/S4g/q7QTNiCm8DVD/DG2Vw7QgNiBh1hqIzyP82XmiwhFY+K2VhTFfkziIGYi4l3jfQ2HJMdxRHxP4iZxGh8wGxFfSiUhduKe+smWrvIBdnf4Q3vwtClmBbcxlrUF6uf+2w57vIT7mMYRHMMlPMJyh7hXGM4BuNMlyfrxGx0BsA9/BwiwiF2djuOrGG6bLNXYVlxJXzQDnBmgeHuN5PeP6u3mU9Z/YajdFhzQ282nrI1i79ogBRjfBPEWrRRg2yYCrGulADl3g6psqOUB7zcR4OP6U1IFI/hj8FXwE7WWKoiIZVwf0IpTuxYRq+ujplY8jBc9rCrXn0h6wIYXElzADD5pPZLL+Kr6ReU5zrbT+g/lp72yFoh52gAAAABJRU5ErkJggg==";
    private final String imageContent = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAABFNAAARTQHAOWBjAAAB/0lEQVRIiWL4//8/xZiBgcGAgYHhAQMDA4gDwgcYGBgEwGb//88AAAAA//+ilgUfkCyA4Qtgi/7/ZwAAAAD//6KVBQiLGBgEAAAAAP//otSSCXgsgGAGhgMAAAAA//+ixAcToOwFeC1hYPgPAAAA//+iNIgWELSIgeE/AAAA//+iRhzgt4iB4QAAAAD//6JWJOOy6AMDA4MBAAAA//8i1wIQOwCaenBZBLbg////DAAAAAD//yLXApAYvqADpTqwBf///2cAAAAA//+ipgUwDE51cPz/PwMAAAD//6K2BfAgguP//xkAAAAA//+iuQX///9nAAAAAP//orkF////ZwAAAAD//6K5Bf///2cAAAAA//8ilA8aKLXg////DAAAAAD//0K2BFQHECrsSLbg////DAAAAAD//4JZkIBWPCNXQBRZ8P//fwYAAAAA//+CWYJcHCgQ8BVJFvz//58BAAAA//+CWQIL8wtI8QOyCN1HJFvw//9/BgAAAAD//wIZ6IAtt0J9hFw2kWXB////GQAAAAD//2KBFnQw8IGRkRFU7oDE5JHEP4Ic8///f5ClpAEGBgYAAAAA//8CYWTXYq8PGBgUyPEBGP//zwAAAAD//wJZgityQYkB5HqyDQfj//8ZAAAAAP//Qs4fIB+BMx81DIbj//8ZAAAAAP//AwARtZXpaOI3DgAAAABJRU5ErkJggg==";
    private final String imageDaily = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJYSURBVEhLvZbNS1ZBFIevhRYq2BcE0sYKJALXFS2Lighatq+V0K7+CAP/CME/oIWgEAVtCzLcRRD0qdEHRZuorJ7nOkeO876YmvWDh/vemXPOnTlzZubtadbXEFwojMEh2A8f4BUswCzMwSfYlHbBTXgBvzaAdtrrtyEdh4cQAZ7ABJyB66XNp++22x+2+um/rk7De9DhDVyF3RA6Ad/LM2S/dtrrp79xusoRxAfug/mvtROOlGct7fWLD3XMyFxGijQcgK1Iv/iQ8daskYtmh1PuNoPNSP9InXFb7YGXYKO5rTUI9cxyunaUZ5ZxjGfVuQ2aK6XBKsmLrPM0OCqNb8MBOAiPwMrS/i5ol/ec7VF1xm+myotOWX2QS1km4WT5/RhcXH+7Oc1IlvHsm3K07mR1pzxD3+AcHIbI7VGI9Nj/FX6AZV0r4o3pEAt9DKz/nO+PMAyn2reVgI5OOXI3pDGiTelvHOOpNv5P0EgckfsgNA7RJy6oa/IltYnrFunS3zjRtwzNu/LiUZFnYo0/Bftc2IsQugT3wAOy/kjMJI4g4zfz5cWpZ+2Ft+BMz8MI5OpTo2AKl6BeeOMZd958Ohp1tjyzHJWl6XH+DKyuWtr0rvxco4i34EcMoC5DHqn3wwyYiuewCH4o6zWYNmN8tqHIOMZTbXx3ZNwb3Xa8u13627dORUlnXQPjre54dQNsdNTbeXYZd1X/5RRW//w+CdU3o7nNxWD9/9XNGHIED0AH8TS9BZbkttzxIXPpoRhV9ye0075jDdRW/nftAw/O/L+r3idJTfMbUfvNNr3flUgAAAAASUVORK5CYII=";
    private final String imageRedirect = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAALPSURBVFhH1ZfL6w1RHMCvZwhRFBbkLWEjr9hIoSjPlNfSwubHgqWNWPkDSMpCLBTlsfDIjrxKIiulEAsboizx+cycuc0998zv3hkkn/p0z8w553vOPXMeM63/nQPBxgwNv02ZEWzMsPBbh5m4AbfhJpyDU3AafsfP2Dd1RmA73sWbuA9H47fgKNyLN9Aylv1jzMd7+BRteAIWHA8WmGeZJ2gd6/4Wm/EdHsUh3og4EoyxrHWsa4xGWPE9rs2u0owMVmFdY9TuhENn7wdrXHp1QIxhrFqPw+fnEFYxCc/i86DpyViFsYzZF85gJ1zqmctYfIxncEHQtPfMS2EsY7p0e+IyciZXcQiv5ckOvHc4TybZj8buIN4H3GTcUFzrVSxH13uM95blySTmG9s22sQdMPgb/JJdpfmEs/NkB7PQvCqMaWzbaBN3YB6+yJOVnMMtuCK7yjG9Fc0bjJc4N0/mODnsxB6ciLvQnt5B9/RL+APLOIxXcCXe9gZ4Nrj7OYE/eKNEOf56dLe8jFl8M9XnshAt5HIy7b14hHbgA7Sx3XghaPoR3sedWKYc39h2oCp+6xiezJNdLMK3uDq7SmOeZRZnV90Y2zbaxD14jUvyZBcuv/PoCFRhnmUsm8LYtlGJM/kVlk+8Aod3TZ4cFEfBsjHGNLZttIlHwGXyEePDo5isvnD0wjKWjXdSYxrbNtp0TQI4jQMYBxiO03Fq+E1Z5I3AMsbysRi7g7iRAg+OW3gqu8q5iEvRf1hV7yeOwWfo0ivwMNqI67KrPkgdx/6r8ZHjgvH98gg0Oo6lnxeSXli30QtJgRXtvUOYGvaq7wLLWse6jRsvcOiKl1KP1PISPREsMM+j3LJ9vZRWTaYU7vMH0bPApeShtQrlIbrJuMZdas72q9iTOh0ocA/3SPVU81SU6+gO5xnRsc7/NvF3QW1SG1Edvgb/GX6SaUNarV8yHYTxxkCURwAAAABJRU5ErkJggg==";

    public UserService(DashConfig config) {
        this.config = config;
    }


    private final String tableBase = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html charset=UTF-8\" />\n" +
            "    <title>Analyse ihrer Marktplatz Präsens</title>\n" +
            "    <style type=\"text/css\">\n" +
            "        .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td {line-height: 100%;}\n" +
            "        .ExternalClass {width: 100%;}\n" +
            "    </style>\n" +
            "</head>\n" +
            "    <body style=\"width: 100%\">\n" +
            "        <table style=\"width: 100%\">\n" +
            "            <tr>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td style=\"background: white;\">\n" +
            "                    <table>\n" +
            "                        <tr>\n" +
            "                            <td><img src=\"../frontend/src/assets/it-sicherheit-logo_w300.png\"/></td>\n" +
            "                            <td class=\"spacer\" style=\"width: 100%\"></td>\n" +
            "                            <td><p style=\"white-space: nowrap\"><b>Das Maktplatz IT-Sicherheit Analyse-Team</b></p></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <table class=\"divider\" style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr style=\"border-bottom: 1px solid #cccccc\">\n" +
            "                                        <td></td>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"quarterStats\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <p class=\"chapter-title\" style=\"text-align: center;font-size: x-large;width: 100%;margin:0;\">Ihr Zuwachs im letzten Quartal:</p><br>\n" +
            "                    <table id=\"quarterStatsList\" >\n" +
            "                        <tr id=\"qs-1\">\n" +
            "                            <td>Profilaufrufe: {{PROFILEVIEWSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/profile_views_25.png\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"qs-2\">\n" +
            "                            <td style=\"padding-left: 4em;\">Weiterleitungen zur eigenen Homepage: {{REDIRECTSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/target_32.png\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"qs-3\">\n" +
            "                            <td>Inhaltsaufrufe: {{CONTENTVIEWSQUARTER}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/pencil-solid.png\"/></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <table class=\"divider\" style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr style=\"border-bottom: 1px solid #cccccc\">\n" +
            "                                        <td></td>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"baseStats\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <p class=\"chapter-title\" style=\"text-align: center;font-size: x-large;width: 100%;margin:0;\">Ihre Gesamtübersicht:</p><br>\n" +
            "                    <table id=\"baseStatsList\">\n" +
            "                        <tr id=\"bs-1\">\n" +
            "                            <td>Profilaufrufe: {{PROFILEVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/profile_views_25.png\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td style=\"padding-left: 4em;\" id=\"bs-1-1\">Tägliche Aufrufe: {{DAILYVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/24-hours_x25.png\"/></td>\n" +
            "                        </tr>\n" +
            "                        <tr>\n" +
            "                            <td style=\"padding-left: 4em;\" id=\"bs-1-2\">Weiterleitungen zur eigenen Homepage: {{REDIRECTS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/target_32.png\"/></td>\n" +
            "                        </tr>\n" +
            "\n" +
            "                        <tr id=\"bs-2\">\n" +
            "                            <td>Inhaltsaufrufe: {{CONTENTVIEWS}}<img style=\"height: 1em;width: auto;aspect-ratio: 1/1;\" height=\"25\" width=\"25\" src=\"https://analyse.it-sicherheit.de/assets/pencil-solid.png\"/></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <table class=\"divider\" style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr style=\"border-bottom: 1px solid #cccccc\">\n" +
            "                                        <td></td>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td id=\"rankings\" class=\"chapter\" style=\"background: white;\">\n" +
            "                    <table id=\"rankingsList\">\n" +
            "                        <tr id=\"rs-1\">\n" +
            "                            <td>Ihre Platzierung nach Profilaufrufen: #{{PROFILERANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"rs-2\">\n" +
            "                            <td>Innerhalb des gleichen Packets: #{{GROUPPROFILERANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr><td style=\"height: 1em\"></td></tr>\n" +
            "                        <tr id=\"rs-3\">\n" +
            "                            <td>Platzierung nach Inhaltsaufrufen: #{{CONTENTRANK}}</td>\n" +
            "                        </tr>\n" +
            "                        <tr id=\"rs-4\">\n" +
            "                            <td>Innerhalb des gleichen Packets: #{{GROUPCONTENTRANK}}</td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <table id=\"rankingsTable\" style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr style=\"border-bottom: 1px solid #cccccc\">\n" +
            "                                        <th>Gewählte Themen</th>\n" +
            "                                        <th>Ihre Platzierung</th>\n" +
            "                                        <th>Globale Nutzungshäufigkeit</th>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                    <tbody>\n" +
            "                                    <!--<tr><td class=\"rankings-1\">-</td><td class=\"rankings-2\" style=\"text-align: center;\">-</td><td class=\"rankings-3\" style=\"text-align: center;\">-</td></tr> -->\n" +
            "                                    {{TABLEROW}}\n" +
            "                                    </tbody>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                    <!--rankingsTable-->\n" +
            "                    <table class=\"divider\" style=\"width: 100%\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                            <td>\n" +
            "                                <table style=\"border-collapse: collapse;width: 100%\">\n" +
            "                                    <thead>\n" +
            "                                    <tr style=\"border-bottom: 1px solid #cccccc\">\n" +
            "                                        <td></td>\n" +
            "                                    </tr>\n" +
            "                                    </thead>\n" +
            "                                </table>\n" +
            "                            </td>\n" +
            "                            <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "            <tr style=\"width: 100%\">\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "                <td style=\"background: white;\">\n" +
            "                    <table style=\"width: 100%;\">\n" +
            "                        <tr>\n" +
            "                            <td class=\"spacer\" style=\"width: 10%\"></td>\n" +
            "                            <td style=\"width: 80%\"><p style=\"text-align: center\">Der Marktplatz IT-Sicherheit\n" +
            "                                Westfälische Hochschule Neidenburger Straße 43 D-45897 Gelsenkirchen<br>\n" +
            "                                © 2022 - 2024 Marktplatz IT-Sicherheit. Alle Rechte vorbehalten</p></td>\n" +
            "                            <td class=\"spacer\" style=\"width: 10%\"></td>\n" +
            "                        </tr>\n" +
            "                    </table>\n" +
            "                </td>\n" +
            "                <td class=\"spacer\" style=\"width: 2.5%\"></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </body>\n" +
            "</html>";

    private final String tablerowBase = "<tr><td class='rankings-1'>REPLACE1</td><td style=\"text-align:center;\" class='rankings-2'>REPLACE2</td><td style=\"text-align:center;\" class='rankings-3'>REPLACE3</td></tr>";

    private final String tablerowCSS = "border-bottom: 1px solid #ddd;";

    /**
     * Generates Newsletters and automatically sends them.
     * @throws JSONException .
     */
    @Scheduled(cron = "0 0 0 1 */3 ?")
    public void generateMails() throws JSONException {
        generateMailsPlus();
        generateMailsPremium();

        //Automatic sending disabled until final version
        //sendNewsletters();
    }

    /**
     * Sends the stat-newsletters via the wordpress plugin.
     * @return whether the sending worked.
     */
    public boolean sendNewsletters() {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(config.getNewslettersend());
            HttpResponse response2 = httpClient.execute(httpPost);
            System.out.println(response2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void generateMailsPremium() throws JSONException {
        for(WPUser user : userRepo.getByAboType("premium")) {
            generateMailSingle(Math.toIntExact(user.getId()));
        }
    }

    private void generateMailsPlus() throws JSONException {
        for(WPUser user : userRepo.getByAboType("plus")) {
            generateMailSingle(Math.toIntExact(user.getId()));
        }
    }

    /**
     * Generate a stat-update mail for the specified user.
     * @param userId the users' id.
     * @return whether the generation was successful.
     */
    public void generateMailSingle(int userId) throws JSONException {
        WPUser user = userRepo.findById((long) userId).get();
        JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
        StatMails statMail;
        if(statMailsRepo.findByUserId(user.getId()) == null) {
            statMail = new StatMails();
            statMail.setUserId(Math.toIntExact(user.getId()));
        } else {
            statMail = statMailsRepo.findByUserId(user.getId());
        }

        String profileViewsQuarter;
        if(userViewsRepo.getSumForUserThisQuarter(userId) != null && userViewsRepo.getSumForUserPreviousQuarter(userId) != null) {
            profileViewsQuarter = (userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userViewsRepo.getSumForUserThisQuarter(userId) + " - " + userViewsRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            profileViewsQuarter = "0";
        }

        String redirectsQuarter;
        if(userRedirectsRepo.getSumForUserThisQuarter(userId) != null && userRedirectsRepo.getSumForUserPreviousQuarter(userId) != null) {
            redirectsQuarter = (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userRedirectsRepo.getSumForUserThisQuarter(userId) + " - " + userRedirectsRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            redirectsQuarter = "0";
        }

        String contentViewsQuarter;
        if(postClicksRepo.getSumForUserThisQuarter(userId) != null && postClicksRepo.getSumForUserPreviousQuarter(userId) != null) {
            contentViewsQuarter = (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId)) + "(" + postClicksRepo.getSumForUserThisQuarter(userId) + " - " + postClicksRepo.getSumForUserPreviousQuarter(userId) + ")";
        } else {
            contentViewsQuarter = "0";
        }

        String rankingTable = makeRankingTable("plus", Math.toIntExact(user.getId()));
        String content;
        if(rankingTable.isBlank()) {
             content = tableBase.replace("{{TABLEROW}}",  rankingTable)
                    .replace("{{PROFILEVIEWS}}", obj.getString("profileViews"))
                    .replace("{{DAILYVIEWS}}", obj.getString("viewsPerDay"))
                    .replace("{{REDIRECTS}}", obj.getString("redirects"))
                    .replace("{{CONTENTVIEWS}}", obj.getString("postViews"))
                    .replace("{{PROFILERANK}}", obj.getString("rankingProfile"))
                    .replace("{{GROUPPROFILERANK}}", obj.getString("rankingProfileByGroup"))
                    .replace("{{CONTENTRANK}}", obj.getString("rankingContent"))
                    .replace("{{GROUPCONTENTRANK}}", obj.getString("rankingContentByGroup"))
                    .replace("{{PROFILEVIEWSQUARTER}}", profileViewsQuarter)
                    .replace("{{REDIRECTSQUARTER}}", redirectsQuarter)
                    .replace("{{CONTENTVIEWSQUARTER}}", contentViewsQuarter)
                    .replace("{{IMAGEDAILY}}", imageDaily)
                    .replace("{{IMAGEPROFILE}}", imageProfile)
                    .replace("{{IMAGEREDIRECTS}}", imageRedirect)
                    .replace("{{IMAGECONTENT}}", imageContent)
                    .replace("<tr>", "<tr style=\"" + tablerowCSS + "\">")
                    ;
        } else {
            //Add Ranking Table Data
            content = tableBase.replace("{{TABLEROW}}", rankingTable)
                    .replace("{{PROFILEVIEWS}}", obj.getString("profileViews"))
                    .replace("{{DAILYVIEWS}}", obj.getString("viewsPerDay"))
                    .replace("{{REDIRECTS}}", obj.getString("redirects"))
                    .replace("{{CONTENTVIEWS}}", obj.getString("postViews"))
                    .replace("{{PROFILERANK}}", obj.getString("rankingProfile"))
                    .replace("{{GROUPPROFILERANK}}", obj.getString("rankingProfileByGroup"))
                    .replace("{{CONTENTRANK}}", obj.getString("rankingContent"))
                    .replace("{{GROUPCONTENTRANK}}", obj.getString("rankingContentByGroup"))
                    .replace("{{PROFILEVIEWSQUARTER}}", profileViewsQuarter)
                    .replace("{{REDIRECTSQUARTER}}", redirectsQuarter)
                    .replace("{{CONTENTVIEWSQUARTER}}", contentViewsQuarter)
                    .replace("{{IMAGEDAILY}}", imageDaily)
                    .replace("{{IMAGEPROFILE}}", imageProfile)
                    .replace("{{IMAGEREDIRECTS}}", imageRedirect)
                    .replace("{{IMAGECONTENT}}", imageContent)
                    .replace("<tr>", "<tr style=\"" + tablerowCSS + "\">");

        }

        StringBuilder cutter = new StringBuilder(content);
        cutter.replace(content.indexOf("<table id=\"rankingsTable\""), content.indexOf("<!--rankingsTable-->"), "");

        statMail.setContent(cutter.toString());
        statMail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        statMail.setSent(0);

        statMailsRepo.save(statMail);
    }

    private String makeRankingTable(String accType, int userId) throws JSONException {
        StringBuilder content = new StringBuilder();

        String row = tablerowBase;

        JSONArray array = new JSONArray(getSingleUserTagsData(userId, "profile"));

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            row = row.replace("REPLACE1", obj.getString("name")).replace("REPLACE2", obj.getString("ranking")).replace("REPLACE3", obj.getString("count"));
            content.append(row);
        }

        return content.toString();
    }

    /**
     * Fetches tags data for a single user.
     * @param id the users' id to fetch for.
     * @param sorter what to sort the data with.
     * @return a JSON-String containing tags-data.
     * @throws JSONException .
     */
    public String getSingleUserTagsData(long id, String sorter) throws JSONException {
        JSONObject ranking = new JSONObject(getRankingsInTagsForUserBySorter(id, sorter));
        JSONObject percentage = getPercentageForTagsByUserId(id);
        var jsonKeys = ranking.keys();
        JSONArray array = new JSONArray();

        while (jsonKeys.hasNext()) {
            String tag = jsonKeys.next().toString();
            JSONObject tempJson;
            int companyCount = new ArrayList<>(Arrays.stream(getCompetitionByTags(id).get(tag).split(",")).toList()).size();
            try {
                tempJson = new JSONObject().put("percentage", percentage.getDouble(tag));
            } catch (Exception e) {
                e.printStackTrace();
                tempJson = new JSONObject().put("percentage", -1);
            }
            try {
                tempJson.put("ranking", ranking.getInt(tag)).put("name", tag);
            } catch (Exception e) {
                e.printStackTrace();
                tempJson.put("ranking", -1);
            }
            tempJson.put("count", companyCount);

            array.put(tempJson);
        }
        return array.toString();

    }

    /**
     * Fetch the rankings of this user in their defined tags.
     * @param id the users' id.
     * @param sorter what to sort the rankings by.
     * @return a JSON-String containing rankings by term.
     * @throws JSONException .
     */
    public String getRankingsInTagsForUserBySorter(long id, String sorter) throws JSONException {
        Map<String, String> competition = getCompetitionByTags(id);
        String thisCompanyName = userRepo.getDisplayNameById(id);

        JSONObject json = new JSONObject();

        for(String key : competition.keySet()) {
            List<String> companyNames = new ArrayList<>(Arrays.stream(competition.get(key).split(",")).toList());

            for(String name : companyNames) {
                if(name.startsWith(" ")) {
                    companyNames.set(companyNames.indexOf(name), name.replaceFirst(" ", ""));
                }
            }

            if(sorter.equalsIgnoreCase("content")) {
                json.put(key, getRankingInListByContentView(thisCompanyName, companyNames));
            } else if(sorter.equalsIgnoreCase("profile")){
                json.put(key, getRankingInListByProfileView(thisCompanyName, companyNames));
            } else {
                json.put(key, getRankingInListByProfileView(thisCompanyName, companyNames));
            }

        }
        return json.toString();

    }

    /**
     * Berechnet den prozentualen Anteil der Anbieter für die Tags eines spezifischen Benutzers.
     *
     * @param userId Die ID des Benutzers, dessen Tag-Prozentsätze abgerufen werden sollen.
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */
    public JSONObject getPercentageForTagsByUserId(Long userId) throws JSONException {
        JSONObject tagPercentages = new JSONObject();
        Optional<String> tagData = getTags(userId, getTypeProfileTags(Math.toIntExact(userId)));

        if (tagData.isPresent()) {
            List<String> rawTags = Arrays.asList(tagData.get().split(";"));
            List<List<String>> decryptedTags = decryptTagsStringInList(rawTags);
            for(List<String> tags : decryptedTags) {
                for (String tag : tags) {
                    double percentage = getUserCountAsPercentageForSingleTag(tag);
                    tagPercentages.put(tag, percentage);
                }
            }
        }
        return tagPercentages;
    }

    /**
     * Ruft eine Zuordnung von Tags zu konkurrierenden Benutzern basierend auf den Tags eines gegebenen Benutzers ab.
     * Diese Methode findet konkurrierende Benutzer für jeden Tag. Konkurrierende Benutzer werden anhand ihrer Anzeigenamen identifiziert.
     *
     * @param userId Die ID des Benutzers, dessen Tags verwendet werden, um Konkurrenz zu finden.
     * @return Eine Map, bei der Schlüssel Tags und Werte Zeichenketten sind, die Listen von Anzeigenamen konkurrierender Benutzer darstellen.
     */
    public Map<String,String> getCompetitionByTags(Long userId){
        Map<String, String> tagsWithCompetingUsers = new HashMap<>();
        Optional<String> tagData = getTags(userId, getTypeProfileTags(Math.toIntExact(userId)));

        if (tagData.isPresent()) {
            List<String> rawTags = Arrays.asList(tagData.get().split(";"));
            List<List<String>> decryptedTags = decryptTagsStringInList(rawTags);

            for(List<String> tags : decryptedTags) {
                for (String tag : tags) {
                    List<Long> competingUserIdsWithTag = getUserIdsByTag(tag);
                    List<String> competingUsersWithTag = userRepo.findAllDisplayNameByIdIn(competingUserIdsWithTag);
                    tagsWithCompetingUsers.put(tag, competingUsersWithTag.toString());
                }
            }
        }

        return tagsWithCompetingUsers;
    }

    private int getRankingInListByContentView(String companyName, List<String> otherCompanies) {
        List<String> allCompaniesList = new ArrayList<>(otherCompanies);
        if(!allCompaniesList.contains(companyName)) {
            allCompaniesList.add(companyName);
        }
        try {
            allCompaniesList.sort((o1, o2) -> Math.toIntExact((int)(userRepo.findByDisplayName(o2).isPresent() ?
                    postService.getPostViewsOfUserById(userRepo.findByDisplayName(o2).get().getId()) : 0)
                    - (userRepo.findByDisplayName(o1).isPresent() ?
                    postService.getPostViewsOfUserById(userRepo.findByDisplayName(o1).get().getId()) : 0)));

            return allCompaniesList.indexOf(companyName) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getRankingInListByProfileView(String companyName, List<String> otherCompanies) {
        List<String> allCompaniesList = new ArrayList<>(otherCompanies);
        if(!allCompaniesList.contains(companyName)) {
            allCompaniesList.add(companyName);
        }
        try {
            allCompaniesList.sort((o1, o2) -> {
                int value1 = 0;
                int value2 = 0;
                if(userRepo.findByDisplayName(o2).isPresent()) {
                    value2 = userStatsRepository.findByUserId(userRepo.findByDisplayName(o2).get().getId()) != null ? Math.toIntExact(userStatsRepository.findByUserId(userRepo.findByDisplayName(o2).get().getId()).getProfileView()) : 0;
                }
                if(userRepo.findByDisplayName(o1).isPresent()) {
                    value1 = userStatsRepository.findByUserId(userRepo.findByDisplayName(o1).get().getId()) != null ? Math.toIntExact(userStatsRepository.findByUserId(userRepo.findByDisplayName(o1).get().getId()).getProfileView()) : 0;
                }
                return value2 - value1;
            });
            return allCompaniesList.indexOf(companyName) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    private String getTypeProfileTags(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return "plus";
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return "basis_plus";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return "premium";
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return "basis";
            if (wpUserMeta.contains("anbieter")) return "none";
        }


        return "none";
    }

    private double getUserCountAsPercentageForSingleTag(String tag) {
        int totalUsersWithTag = getTotalCountOfUsersWithTags();
        int countForTag = countUsersByTag(tag);

        if (totalUsersWithTag == 0) {
            return 0; // Vermeidung der Division durch null
        }

        return (double) countForTag / totalUsersWithTag * 100;
    }

    private List<Long> getUserIdsByTag(String tag) {
        List<Long> list = new ArrayList<>();
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasis("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasisPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPremium("\"" + tag + "\""));
        return list;
    }

    private Integer getTotalCountOfUsersWithTags() {
        return wpUserMetaRepository.getTotalCountOfUsersWithTagBasis() + wpUserMetaRepository.getTotalCountOfUsersWithTagBasisPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPremium();
    }

    /**
     * Count the users in the given tag.
     * @param tag the tag to search in.
     * @return amount of users with this tag.
     */
    public Integer countUsersByTag(String tag) {
        return wpUserMetaRepository.countUsersByTagBasis("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagBasisPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPremium("\"" + tag + "\"");
    }

    /**
     * Fetch all stats for a single user.
     * @param id the users' id.
     * @return a JSON-Object containing detailed userdata.
     * @throws JSONException .
     */
    public String getAllSingleUser(long id) throws JSONException {
        JSONObject obj = new JSONObject();
        WPUser user = userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null;
        if(user != null) {
            obj.put("id", user.getId());
            obj.put("email", user.getEmail());
            obj.put("displayName", user.getDisplayName());
            obj.put("niceName", user.getNicename());
            obj.put("creationDate", user.getRegistered().toLocalDate().toString());
            if (userStatsRepository.existsByUserId(user.getId())) {
                UserStats statsUser = userStatsRepository.findByUserId(user.getId());
                obj.put("profileViews", statsUser.getProfileView());
                obj.put("postViews", postService.getPostViewsOfUserById(user.getId()));
                obj.put("postCount", postService.getPostCountOfUserById(user.getId()));
            } else {
                obj.put("profileViews", 0);
                obj.put("postViews", 0);
                obj.put("postCount", 0);
                obj.put("performance", 0);
            }
            if (userViewsRepo.existsByUserId(user.getId())) {
                obj.put("viewsPerDay", getUserClicksPerDay(user.getId()));
                if (tendencyUp(user.getId()) != null) {
                    obj.put("tendency", tendencyUp(user.getId()));
                }
            } else {
                obj.put("viewsPerDay", 0);
                obj.put("tendency", 0);
            }

            //Does User have a made in EU badge
            if (wpUserMetaRepository.getTeleEU(user.getId()).isEmpty()) {
                obj.put("TeleEU", false);
            } else {
                obj.put("TeleEU", wpUserMetaRepository.getTeleEU(user.getId()).get().contains("a:1:{"));
            }
            if (wpUserMetaRepository.getTeleDE(user.getId()).isEmpty()) {
                obj.put("TeleDE", false);
            } else {
                obj.put("TeleDE", wpUserMetaRepository.getTeleDE(user.getId()).get().contains("a:1:{"));
            }

            //Does User have a made in DE badge
            if (wpUserMetaRepository.getCompanyCategory(user.getId()).isEmpty()) {
                obj.put("category", "none");
            } else {
                obj.put("category", getCompanyCategoryFromString(wpUserMetaRepository.getCompanyCategory(user.getId()).get()));
            }

            //checks how many employees a company has.
            if (wpUserMetaRepository.getCompanyEmployees(user.getId()).isEmpty()) {
                obj.put("employees", "");
            } else {
                obj.put("employees", wpUserMetaRepository.getCompanyEmployees(user.getId()).get());
            }

            //Checks how many times website has redirected to a user's homepage
            if(userRedirectsRepo.existsByUserId(user.getId())) {
                obj.put("redirects", userRedirectsRepo.getAllRedirectsOfUserIdSummed(user.getId()));
            } else {
                obj.put("redirects", 0);
            }

            Pattern pattern = Pattern.compile("\"([^\"]+)\"");

            if (wpUserMetaRepository.getService(user.getId()).isEmpty()) {
                obj.put("service", "none");
            } else {
                JSONArray json = new JSONArray();
                Matcher matcher = pattern.matcher(wpUserMetaRepository.getService(user.getId()).get());
                if (matcher.find()) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        json.put(matcher.group(i));
                    }
                }
                obj.put("service", json);
            }


            if (getTags(user.getId(), getTypeProfileTags(Math.toIntExact(user.getId()))).isEmpty()) {
                obj.put("tags", "none");
            } else {
                JSONArray json = new JSONArray();
                Matcher matcher = pattern.matcher(getTags(user.getId(), getTypeProfileTags(Math.toIntExact(user.getId()))).get());
                if (matcher.find()) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        json.put(matcher.group(i));
                    }
                }
                obj.put("tags", json);
            }
            obj.put("potential", 0);
            try {
                obj.put("potential", getPotentialPercent(Math.toIntExact(user.getId())));
            } catch (Exception ignored) {
            }

            if(wpUserMetaRepository.getTelIntern(user.getId()).isPresent()) {
                obj.put("tel", wpUserMetaRepository.getTelIntern(user.getId()).get());
            } else if(wpUserMetaRepository.getTelExtern(user.getId()).isPresent()) {
                obj.put("tel", wpUserMetaRepository.getTelExtern(user.getId()).get());
            }

            if(wpUserMetaRepository.getSlogan(user.getId()).isPresent()) {
                obj.put("slogan", wpUserMetaRepository.getSlogan(user.getId()).get());
            } else {
                obj.put("slogan", " - ");
            }

            obj.put("accountType", getType(Math.toIntExact(user.getId())));

            String path = String.valueOf(Paths.get(config.getProfilephotos() + "/" + user.getId() + "/profile_photo.png"));
            String path2 = String.valueOf(Paths.get(config.getProfilephotos() + "/" + user.getId() + "/profile_photo.jpg"));

            String srcUrl = Constants.getInstance().getProfilePhotoStart() + user.getId() + "/profile_photo";

            if (new File(path).exists()) {
                obj.put("img", srcUrl + ".png");
            } else if (new File(path2).exists()) {
                obj.put("img", srcUrl + ".jpg");
            }

            putRankings(id, obj);

            return obj.toString();
        } else {
            return "User not found";
        }
    }

    /**
     * @param userId the user you want to fetch data for.
     * @return a double representing the amount of clicks a user had for each day of tracking (arithmetic average) or zero if user has not been tracked.
     */
    public double getUserClicksPerDay(long userId) {
        int countDays = getDaysSinceTracking(userId);
        long totalClicks = 0;
        int lastUniId = 0;
        for(UserViewsByHourDLC u : userViewsRepo.findByUserId(userId)) {
            if(lastUniId != u.getUniId()) {
                lastUniId = u.getUniId();
            }
            totalClicks+= u.getViews();
        }
        if(countDays > 0) {
            return (double) totalClicks / countDays;
        } else {
            return 0;
        }
    }

    /**
     * Checks whether the tendency of profile views is growing or falling for this user.
     * @param userId the users' id.
     * @return whether the tendency is growth.
     */
    public Boolean tendencyUp(long userId) {
        int count = 7;
        int clicks = 0;
        if(getDaysSinceTracking(userId) > 7) {
            for(Integer uni : userViewsRepo.getLast7Uni()) {
                for(UserViewsByHourDLC u : userViewsRepo.findByUserIdAndUniId(userId, uni)) {
                    clicks += u.getViews();
                }
            }
        } else {
            return null;
        }
        Double avg = ((double) clicks / count);
        if(avg > getUserClicksPerDay(userId)) return true;
        if(avg.equals(getUserClicksPerDay(userId))) return null;
        return false;
    }

    private String getCompanyCategoryFromString(String categoryString) {
        if(categoryString.contains("Startup")) return "startup";
        if(categoryString.contains("Hochschule")) return "hochschule";
        if(categoryString.contains("Mittelstand")) return "mittelstand";
        if(categoryString.contains("Verband")) return "verband";
        if(categoryString.contains("Keine Angabe")) return "keine angabe";
        if(categoryString.contains("Dienstleister")) return "dienstleister";
        if(categoryString.contains("Großkonzern")) return "großkonzern";
        return "none";
    }


    private double getPotentialPercent(int userId) throws JSONException {
        JSONObject json = new JSONObject(getPotentialByID(userId));

        int countFulfilled = 0; int countPossible = 0;
        countPossible+= 1 + 1 + 1 + 1 + json.getInt("tagsMax") + 1 + json.getInt("contactPublicMax") + json.getInt("contactInternMax") + json.getInt("companyDetailsMax") + json.getInt("solutionsMax");
        countFulfilled += json.getInt("profilePicture")
                + json.getInt("titlePicture") + json.getInt("bio") + json.getInt("slogan")
                + json.getInt("tagsCount") + json.getInt("contactPublic")
                + json.getInt("contactIntern") + json.getInt("companyDetails") + json.getInt("solutions");

        return (double) countFulfilled / countPossible;
    }

    /**
     * @param id user id to fetch an account type for.
     * @return "basis" "plus" "premium" "sponsor" "basis-plus" "admin" "none"
     */
    public String getType(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return "plus";
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return "basis-plus";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return "premium";
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return "basis";
            if(wpUserMeta.contains(Constants.getInstance().getMod())) return "moderator";
            if (wpUserMeta.contains("anbieter")) return "none";
        }


        return "none";
    }

    private void putRankings(long id, JSONObject obj) throws JSONException {
        obj.put("rankingContent", getRankingTotalContentViews(id));
        obj.put("rankingContentByGroup", getRankingInTypeContentViews(id));
        obj.put("rankingProfile", getRankingTotalProfileViews(id));
        obj.put("rankingProfileByGroup", getRankingInTypeProfileViews(id));
    }

    private int getDaysSinceTracking(long userId) {
        if(userViewsRepo.existsByUserId(userId)) {
            return (int) (userViewsRepo.getLastUniId() - userViewsRepo.getFirstUniIdByUserid(userId));
        } else {
            return 0;
        }
    }

    /**
     * @param userId  id des users.
     * @return a collection of maximum and actual values for a user's completion status of their profile.
     */
    public String getPotentialByID(int userId) throws JSONException {

        String type = this.getType(userId);
        //Check whether these profile parts have been filled out.
        boolean hasProfilePic = wpUserMetaRepository.getProfilePath(((long) userId)).isPresent() && !wpUserMetaRepository.getProfilePath(((long) userId)).get().equals("https://it-sicherheit.de/wp-content/uploads/2023/06/it-sicherheit-logo_icon_190x190.png");
        boolean hasCover = wpUserMetaRepository.getCoverPath((long) userId).isPresent();
        boolean hasDescription = wpUserMetaRepository.getDescription((long) userId).isPresent();
        boolean hasSlogan = !type.equals("basis") && wpUserMetaRepository.getSlogan((long) userId).isPresent();

        //Check how many internal contacts have been filled.
        int countAnsprechpartnerIntern = 0;
        int maxAnsprechpartnerIntern = 3;
        if(wpUserMetaRepository.getPersonIntern((long) userId).isPresent() && !wpUserMetaRepository.getPersonIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getMailIntern((long) userId).isPresent() && !wpUserMetaRepository.getMailIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;
        if(wpUserMetaRepository.getTelIntern((long) userId).isPresent() && !wpUserMetaRepository.getTelIntern((long) userId).get().isEmpty()) countAnsprechpartnerIntern++;

        //Check how many external contacts have been filled.
        int countKontaktExtern = 0;
        int maxKontaktExtern = 7;
        if(wpUserMetaRepository.getNameExtern((long) userId).isPresent()  && !wpUserMetaRepository.getNameExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getSecondaryMail((long) userId).isPresent() && !wpUserMetaRepository.getSecondaryMail((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getTelExtern((long) userId).isPresent() && !wpUserMetaRepository.getTelExtern((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseStreet((long) userId).isPresent() && !wpUserMetaRepository.getAdresseStreet((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdressePLZ((long) userId).isPresent() && !wpUserMetaRepository.getAdressePLZ((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getAdresseOrt((long) userId).isPresent() && !wpUserMetaRepository.getAdresseOrt((long) userId).get().isEmpty()) countKontaktExtern++;
        if(wpUserMetaRepository.getURLExtern((long) userId).isPresent() && !wpUserMetaRepository.getURLExtern((long) userId).get().isEmpty()) countKontaktExtern++;

        //Check how many tags are allowed, and how many are set.
        int allowedTags = 0;
        int allowedLosungen = 0;
        switch (type) {
            case "basis" -> {
                allowedTags = 1;
                maxKontaktExtern = 6;
            }
            case "basis-plus" -> allowedTags = 3;
            case "plus" -> {
                allowedTags = 8;
                allowedLosungen = 5;
            }
            case "premium" -> {
                allowedTags = 12;
                allowedLosungen = 12;
            }
            case "admin" -> {
                allowedTags = 100;
                allowedLosungen = 100;
            }
        }


        int countTags = new JSONArray(getSingleUserTagsData(userId, "profile")).length();

        //Check how many solutions are allowed, and how many are set.
        int solutions = 0;
        for(int i = 0; i < allowedLosungen; i++) {
            switch(i) {
                case(0) -> {
                    if(wpUserMetaRepository.getSolutionHead1((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead1((long) userId).get().isBlank()) solutions ++;
                }
                case(1) -> {
                    if(wpUserMetaRepository.getSolutionHead2((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead2((long) userId).get().isBlank()) solutions ++;
                }
                case(2) -> {
                    if(wpUserMetaRepository.getSolutionHead3((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead3((long) userId).get().isBlank()) solutions ++;
                }
                case(3) -> {
                    if(wpUserMetaRepository.getSolutionHead4((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead4((long) userId).get().isBlank()) solutions ++;
                }
                case(4) -> {
                    if(wpUserMetaRepository.getSolutionHead5((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead5((long) userId).get().isBlank()) solutions ++;
                }
                case(5) -> {
                    if(wpUserMetaRepository.getSolutionHead6((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead6((long) userId).get().isBlank()) solutions ++;
                }
                case(6) -> {
                    if(wpUserMetaRepository.getSolutionHead7((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead7((long) userId).get().isBlank()) solutions ++;
                }
                case(7) -> {
                    if(wpUserMetaRepository.getSolutionHead8((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead8((long) userId).get().isBlank()) solutions ++;
                }
                case(8) -> {
                    if(wpUserMetaRepository.getSolutionHead9((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead9((long) userId).get().isBlank()) solutions ++;
                }
                case(9) -> {
                    if(wpUserMetaRepository.getSolutionHead10((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead10((long) userId).get().isBlank()) solutions ++;
                }
                case(10) -> {
                    if(wpUserMetaRepository.getSolutionHead11((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead11((long) userId).get().isBlank()) solutions ++;
                }
                case(11) -> {
                    if(wpUserMetaRepository.getSolutionHead12((long) userId).isPresent() && !wpUserMetaRepository.getSolutionHead12((long) userId).get().isBlank()) solutions ++;
                }
            }
        }

        //Check how many company datafields have been filled.
        int companyDetails = 0;
        int companyDetailsMax = 4;
        if(wpUserMetaRepository.getCompanyCategory((long) userId).isPresent() && !wpUserMetaRepository.getCompanyCategory((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getManager((long) userId).isPresent() && !wpUserMetaRepository.getManager((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getCompanyEmployees((long) userId).isPresent() && !wpUserMetaRepository.getCompanyEmployees((long) userId).get().isEmpty()) companyDetails++;
        if(wpUserMetaRepository.getService((long) userId).isPresent() && !wpUserMetaRepository.getService((long) userId).get().isEmpty()) companyDetails++;


        JSONObject json = new JSONObject();
        json.put("profilePicture", hasProfilePic ? 1 : 0);
        json.put("titlePicture", hasCover ? 1 : 0);
        json.put("bio", hasDescription ? 1 : 0);
        json.put("slogan", hasSlogan ? 1 : 0);
        json.put("tagsCount", countTags);
        json.put("tagsMax", allowedTags);
        json.put("contactPublic", countKontaktExtern);
        json.put("contactPublicMax", maxKontaktExtern);
        json.put("contactIntern", countAnsprechpartnerIntern);
        json.put("contactInternMax", maxAnsprechpartnerIntern);
        json.put("companyDetails", companyDetails);
        json.put("companyDetailsMax", companyDetailsMax);
        json.put("solutions", solutions);
        json.put("solutionsMax", allowedLosungen);

        return json.toString();
    }

    private int getRankingInTypeProfileViews(long id) {
        return rankingGroupProfileRepo.getRankById(id).isPresent() ? rankingGroupProfileRepo.getRankById(id).get() : -1;
    }

    private int getRankingInTypeContentViews(long id) {
        return rankingGroupContentRepo.getRankById(id).isPresent() ? rankingGroupContentRepo.getRankById(id).get() : -1;
    }

    private int getRankingTotalProfileViews(long id) {
        return rankingTotalProfileRepo.getRankById(id).isPresent() ? rankingTotalProfileRepo.getRankById(id).get() : -1;
    }

    private int getRankingTotalContentViews(long id)  {
        return rankingTotalContentRepo.getRankById(id).isPresent() ? rankingTotalContentRepo.getRankById(id).get() : -1;
    }

    /**
     *
     * @param page which page of results in the given size you want to fetch.
     * @param size the number of results you want per page.
     * @param search the search-term you want results for, give empty string for none.
     * @param filterAbo "basis" "basis-plus" "plus" "premium" "sponsor" "none" "admin"
     * @param sorter "profileView" "contentView" "viewsByTime", any other String searches by user id.
     * @return a JSON String containing information about all users in the specified page, and the number of users loaded.
     * @throws JSONException .
     */
    public String getAll(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter) throws JSONException {

        JSONArray response = new JSONArray();
        List<WPUser> list = fetchUserListByCriteria(page, size, search, filterAbo, filterTyp, sorter);

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }

    private List<WPUser> fetchUserListByCriteria(Integer page, Integer size, String search, String filterAbo, String filterTyp, String sorter) {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAll(search, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAll(search, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAll(search, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAbo(search, filterAbo, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeCompany(search, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepo.getAllByNicenameContainingAll(search, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepo.getAllByNicenameContainingAbo(search, filterAbo, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepo.getAllByNicenameContainingCompany(search, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepo.getAllByNicenameContainingAboAndCompany(search, filterAbo, filterTyp, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }
        return list;
    }

    /**
     * Fetch all users with tags associated with their profile.
     * @param page the page of results.
     * @param size the amount of results to fetch .
     * @param search searches in name and id for matches.
     * @param filterAbo only displays users of the given membership (leave empty for no filter).
     * @param filterTyp only displays users of the given category (leave empty for no filter).
     * @param sorter what to sort users by.
     * @return a JSON-Array of JSON-Objects containing userdata.
     * @throws JSONException .
     */
    public String getAllWithTagsTest(Integer page, Integer size, String search, String filterAbo, String filterTyp, String tag, String sorter) throws JSONException {
        List<WPUser> list;


        if(sorter != null) {
            //Both filters unused, sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAllWithTags(search, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboWithTags(search, filterAbo, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Type Filter used, sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            } else {
                //Abo, Company type and sorter used.
                switch (sorter) {
                    case "profileView" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "contentView" -> {
                        list = userRepo.getAllNameLikeAndContentViewsAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    case "viewsByTime" -> {
                        list = userRepo.getAllNameLikeAndProfileViewsByTimeAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size));
                    }
                    default -> {
                        list = userRepo.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
                    }
                }
            }
        } else {
            //Neither filters nor sorter used.
            if(filterAbo.isBlank() && filterTyp.isBlank()) {
                list = userRepo.getAllByNicenameContainingAllWithTags(search, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(!filterAbo.isBlank() && filterTyp.isBlank()) {
                //Abo-Filter used.
                list = userRepo.getAllByNicenameContainingAboWithTags(search, filterAbo, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else if(filterAbo.isBlank() && !filterTyp.isBlank()) {
                //Company-Filter used.
                list = userRepo.getAllByNicenameContainingCompanyWithTags(search, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            } else {
                //Both filters used, no sorter used.
                list = userRepo.getAllByNicenameContainingAboAndCompanyWithTags(search, filterAbo, filterTyp, tag, PageRequest.of(page, size, Sort.by("id").descending()));
            }
        }

        JSONArray response = new JSONArray();

        for(WPUser user : list) {
            JSONObject obj = new JSONObject(getAllSingleUser(user.getId()));
            response.put(obj);
        }
        return new JSONObject().put("users", response).put("count", list.size()).toString();
    }

    /**
     * Fetch some user-data by id.
     * @param id the users' id.
     * @return a JSON-String containing some userdata.
     * @throws JSONException .
     */
    public String getUserById(int id) throws JSONException {
        JSONObject obj = new JSONObject();
        WPUser user = userRepo.findById((long) id).orElseThrow();

        obj.put("id", user.getId());
        obj.put("displayName", user.getDisplayName());
        obj.put("accountType", getType(Math.toIntExact(user.getId())));
        obj.put("accessLevel", getAccessLevel(user.getId()));

        return obj.toString();
    }

    /**
     * Checks the given users access level (admin | mod | premium).
     * @param userId the users' id.
     * @return the access-level (admin | mod | premium).
     */
    public String getAccessLevel(long userId) {
        if(getType((int) userId).equals("admin")) {
            return "admin";
        } else if(isModerator(userId)) {
            return "mod";
        } else if(getType((int) userId).equals("premium")) {
            return "user";
        } else {
            return "none";
        }
    }

    /**
     * Fetch a users data by their login-name.
     * @param u the login-name to fetch for.
     * @return a JSON-String containing some userdata.
     * @throws JSONException .
     */
    public String getUserByLogin(@RequestParam String u) throws JSONException {
        JSONObject obj = new JSONObject();
        var user = userRepo.findByLogin(u);
        if (user.isPresent()){
            obj.put("id", user.get().getId());
            obj.put("displayName",user.get().getDisplayName());
            obj.put("accountType", getType(Math.toIntExact(user.get().getId())));
            obj.put("accessLevel", getAccessLevel(user.get().getId()));
        }
        return obj.toString();
    }

    /**
     * Fetch stats of a user specific for newsletter usage.
     * @param id the users' id.
     * @return a JSON-Object containing userdata.
     */
    public String getAllSingleUserNewsletter(long id) {
        JSONObject obj = new JSONObject();
        try {
            id = newsletterRepo.getWpUserIdById(id);
            WPUser user = userRepo.findById(id).isPresent() ? userRepo.findById(id).get() : null;
            if (user != null) {
                obj.put("id", user.getId());
                obj.put("email", user.getEmail());
                obj.put("displayName", user.getDisplayName());
                obj.put("niceName", user.getNicename());
                obj.put("creationDate", user.getRegistered().toLocalDate().toString());
            }
        } catch (Exception e) {
            return "Error, user kaputt";
        }
        return obj.toString();
    }

    /**
     * Fetch a users profile picture.
     * @param id the users' id.
     * @return the users profile-picture as bytes.
     */
    public ResponseEntity<byte[]> getProfilePic(@RequestParam long id) {

        try {
            String path = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.png"));
            String path2 = String.valueOf(Paths.get(config.getProfilephotos() + "/" + id + "/profile_photo.jpg"));

            File cutePic = new File(path);
            if (!cutePic.exists())
            {
                cutePic = new File(path2);
            }
            byte[] imageBytes = Files.readAllBytes(cutePic.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Fetch data for the user-clicks chart.
     * @param id the users' id.
     * @param start the start of the time frame to show.
     * @param end the end of the time frame to show.
     * @return a JSON-String containing user-clicks-chart data.
     * @throws JSONException .
     */
    public String getUserClicksChartData(long id, String start, String end) throws JSONException {
        java.sql.Date startDate = java.sql.Date.valueOf(start);
        java.sql.Date endDate  = java.sql.Date.valueOf(end);

        //If the beginning is after the end (good manhwa), swap them.
        if(startDate.after(endDate)) {
            java.sql.Date puffer = startDate;
            startDate = endDate;
            endDate = puffer;
        }

        JSONArray json = new JSONArray();
        //For all dates selected, add data
        for(LocalDate date : startDate.toLocalDate().datesUntil(endDate.toLocalDate().plusDays(1)).toList()) {
            int uniId = 0;
            //Check if we have stats for the day
            if (uniRepo.findByDatum(java.sql.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).isPresent()) {
                uniId = uniRepo.findByDatum(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())).get().getId();
            }

            if(uniId != 0 && uniRepo.findById(uniId).isPresent()) {
                //Since we have data, add date and profileViews
                JSONObject day = new JSONObject();
                JSONArray dailyPosts = new JSONArray();
                JSONObject biggestPost = new JSONObject();

                day.put("date", uniRepo.findById(uniId).get().getDatum());
                if(userViewsRepo.existsByUserId(id)) {
                    day.put("profileViews", userViewsRepo.getSumByUniIdAndUserId(uniId, id) != null ? userViewsRepo.getSumByUniIdAndUserId(uniId, id) : 0);
                } else {
                    day.put("profileViews", 0);
                }

                Post biggestPostbuffer = null;
                for(Post post : postRepo.getPostsByAuthorAndDate(id, date)) {
                    //Add data for all posts
                    JSONObject postToday = new JSONObject();
                    if(biggestPostbuffer == null) {
                        biggestPostbuffer = post;
                    } else {
                        if((statRepository.getSumClicks(post.getId())) != null) {
                            if(statRepository.getSumClicks(post.getId()) > statRepository.getSumClicks(biggestPostbuffer.getId())) {
                                biggestPostbuffer = post;
                            }
                        }
                    }
                    postToday.put("id", post.getId());
                    postToday.put("title", post.getTitle());
                    postToday.put("type", postService.getType(Math.toIntExact(post.getId())));
                    postToday.put("clicks", statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0);
                    dailyPosts.put(postToday);
                }
                day.put("posts", dailyPosts);
                if(biggestPostbuffer != null) {
                    biggestPost.put("id", biggestPostbuffer.getId());
                    biggestPost.put("title", biggestPostbuffer.getTitle());
                    biggestPost.put("type", postService.getType(Math.toIntExact(biggestPostbuffer.getId())));
                    biggestPost.put("clicks", statRepository.getSumClicks(biggestPostbuffer.getId()) != null ? statRepository.getSumClicks(biggestPostbuffer.getId()) : 0);
                } else {
                    biggestPost.put("id", 0);
                    biggestPost.put("title", 0);
                    biggestPost.put("type", 0);
                    biggestPost.put("clicks", 0);
                }
                day.put("biggestPost", biggestPost);

                json.put(day);
            }

        }
        return  json.toString();
    }

    /**
     *
     * @return a JSON-String containing a list of Events (newEvents) starting with u| for upcoming, c| for current and their type,
     * the count of events in the past for this user (countOldEvents)
     * and a count of all events by this user that are active (countTotal).
     */
    public String getCountEvents(long id) throws JSONException {

        JSONObject json = new JSONObject();

        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        int countOld = 0;

        for (Events e : allEvents) {
            if(eventsService.isActive(e)) {
                if (eventsService.isCurrent(e)) {
                    events.add("c|" + eventsService.getEventType(e));
                } else if (eventsService.isUpcoming(e)) {
                    events.add("u|" + eventsService.getEventType(e));
                } else {
                    countOld++;
                }
            }

        }
        json.put("newEvents", new JSONArray(events));
        json.put("countOldEvents", countOld);
        json.put("countTotal", countOld + events.size());

        return json.toString();
    }

    /**
     * Fetch posts by their type that were made by the user.
     * @param id the users' id.
     * @return a JSON-String containing post by their types with the users as author.
     * @throws JSONException .
     */
    public String getPostCountByType(long id) throws JSONException {
        List<Post> posts = postRepo.findByAuthorPageable(id, "", PageRequest.of(0, postService.getCountTotalPosts()));

        int countArtikel = 0;
        int countBlogs = 0;
        int countNews = 0;
        int countWhitepaper = 0;
        int countPodcasts = 0;

        for(Post post : posts) {
            switch(postService.getType(post.getId())) {
                case "artikel" -> {
                    countArtikel++;
                }
                case "blog" -> {
                    countBlogs++;
                }
                case "news" -> {
                    countNews++;
                }
                case "podcast" -> {
                    countPodcasts++;
                }
                case "whitepaper" -> {
                    countWhitepaper++;
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("Whitepaper", countWhitepaper);
        json.put("Blogs", countBlogs);
        json.put("News", countNews);
        json.put("Podcasts", countPodcasts);
        json.put("Artikel", countArtikel);

        return json.toString();
    }

    /**
     *
     * @return a List of Strings, each starting with c| (current) or u| (upcoming) and then the name of the event for all events created within the last day, by the given User.
     */
    public List<String> getAmountOfEventsCreatedYesterday(long id) {
        List<String> events = new ArrayList<>();
        List<Events> allEvents = eventsRepo.getAllByOwnerID(id);
        LocalDate today = LocalDate.now();

        for (Events e : allEvents) {
            LocalDate createdDate = e.getEventDateCreated().toLocalDate();

            if (createdDate.isBefore(today) && eventsService.isActive(e)) {
                if(eventsService.isCurrent(e)) {
                    events.add("c|" + eventsService.getEventType(e));
                } else if(eventsService.isUpcoming(e)) {
                    events.add("u|" + eventsService.getEventType(e));
                }

            }
        }
        return events;
    }

    /**
     * Fetch Events made by the user.
     * @param page the page of results.
     * @param size the amount of results.
     * @param filter what Events shall be fetched.
     * @param search a search in events, only displaying matching events.
     * @param id the authors userid.
     * @return a JSON-String containing Events with their stats.
     * @throws JSONException .
     * @throws ParseException .
     */
    public String getEventsWithStats(Integer page, Integer size,  String filter, String search, long id) throws JSONException, ParseException {
        List<Post> list;

        if(filter.isBlank()) {
            list = postRepo.getAllEventsWithSearchAndAuthor(search, id, PageRequest.of(page, size));
        } else {
            list = postRepo.getAllEventsWithTypeAndSearchAndAuthor(eventsService.getTermIdFromFrontendType(filter), search, id, PageRequest.of(page, size));
        }

        List<JSONObject> stats = new ArrayList<>();

        for(Post post : list) {
            stats.add(new JSONObject(postService.PostStatsByIdForFrontend(post.getId())));
        }

        return new JSONArray(stats).toString();
    }

    /**
     * Fetch a users stats.
     * @param userId the user to fetch stats for.
     * @return a JSON-String of userdata.
     */
    public UserStats getUserStats(@PathVariable("userId") Long userId) {
        return userStatsRepository.findByUserId(userId);
    }

    /**
     * Fetch a users stats as string.
     * @param id the user to fetch stats for.
     * @return a JSON-String of userdata.
     */
    public String getUserStat(@RequestParam Long id) throws JSONException {
        JSONObject obj = new JSONObject();
        UserStats user = userStatsRepository.findByUserId(id);
        obj.put("Profilaufrufe",user.getProfileView());
        return obj.toString();
    }

    /**
     * Fetch a users views broken down.
     * @param id the users' id.
     * @return a JSON-String of userdata.
     * @throws JSONException .
     */
    public String getViewsBrokenDown(@RequestParam Long id) throws JSONException {
        long viewsBlog = 0;
        long viewsArtikel = 0;
        long viewsProfile = 0;
        long viewsNews = 0;
        long viewsWP = 0;
        long viewsPodcast = 0;
        long viewsVideo = 0;
        try {
            viewsProfile = userStatsRepository.findByUserId(id).getProfileView();
        } catch (NullPointerException ignored) {
        }

        List<Post> posts = postRepo.findByAuthor(id.intValue());

        for (Post post : posts) {
            if (statRepository.existsByArtId(post.getId())) {
                int stat = statRepository.getSumClicks(post.getId()) == null ? 0 : statRepository.getSumClicks(post.getId());
                switch(postService.getType(post.getId())) {
                    case "blog" -> viewsBlog += stat;
                    case "artikel" -> viewsArtikel += stat;
                    case "news" -> viewsNews += stat;
                    case "whitepaper" -> viewsWP += stat;
                    case "podcast" -> viewsPodcast += stat;
                    case "videos" -> viewsVideo += stat;
                }
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("viewsBlog", viewsBlog);
        obj.put("viewsArtikel", viewsArtikel);
        obj.put("viewsNews", viewsNews);
        obj.put("viewsWhitepaper", viewsWP);
        obj.put("viewsPodcast", viewsPodcast);
        obj.put("viewsVideo", viewsVideo);
        obj.put("viewsProfile", viewsProfile);
        return obj.toString();

    }

    /**
     * Fetch a users profile views averages by membership and post-posession.
     * @return a JSON-String.
     * @throws JSONException .
     */
    public String getUserProfileViewsAveragesByTypeAndPosts() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithoutPosts()));
        array.put(new JSONObject(getUserAveragesByType()));
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        return array.toString();
    }

    /**
     * Fetch a users profile views averages by membership.
     * @return a JSON-String.
     * @throws JSONException .
     */
    public String getUserProfileAndPostViewsAveragesByType() throws JSONException {
        JSONArray array = new JSONArray();
        array.put(new JSONObject(getUserAveragesWithPostsWithoutPostClicks()));
        array.put(new JSONObject(getUserAveragesWithPostsOnlyPostClicks()));
        return array.toString();
    }

    /**
     * Fetch a users profile views averages by membership and post-possesions, skewed towards higher memberships.
     * @return a JSON-String.
     * @throws JSONException .
     */
    public String getUserProfileAndPostViewsAveragesByTypeSkewed() throws JSONException {
        JSONObject json = new JSONObject();

        for(String type : Constants.getInstance().getListOfUserTypes()) {
            JSONObject obj = new JSONObject();

            if(type.contains("basis")) {
                //For low-end users, add profile-views for only those without posts and a zero for post-clicks for format.
                obj.put("profile", new JSONObject(getUserAveragesWithoutPosts()).getDouble(type));
                obj.put("posts", 0);
            } else {
                //For high-end users, add both profile views for users with posts, and their respective posts clicks.
                obj.put("profile", new JSONObject(getUserAveragesWithPostsWithoutPostClicks()).getDouble(type));
                obj.put("posts", new JSONObject(getUserAveragesWithPostsOnlyPostClicks()).getDouble(type));
            }

            json.put(type, obj);
        }
        return json.toString();
    }

    /**
     * This accounts for ONLY users that do not have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    public String getUserAveragesWithoutPosts() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(!hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for all users, whether they have posts or not and ONLY counts profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    public String getUserAveragesByType() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            addCountAndProfileViewsByType(counts, clicks, u, stats);
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);

        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsWithoutPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    private String getUserAveragesWithPostsOnlyPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, false, true);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * Checks whether a user has a post.
     * @param id the users' id.
     * @return whether the user has authored a post.
     */
    public boolean hasPost(@RequestParam int id) {
        return !postRepo.findByAuthor(id).isEmpty();
    }

    /**
     * Checks whether the given user has moderator capabilities.
     * @param userId the users' id.
     * @return whether the user has moderator capabilities.
     */
    public boolean isModerator(long userId) {
        if(wpUserMetaRepository.existsByUserId(userId)) {
            return wpUserMetaRepository.getWPUserMetaValueByUserId(userId).contains("editor");
        }
        return false;
    }

    private void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    private void addCountAndProfileViewsByType(JSONObject counts, JSONObject clicks, WPUser u, boolean profileViews, boolean postViews) throws JSONException {
        switch(getType(Math.toIntExact((u.getId())))) {
            case "basis" -> {
                if(profileViews) {
                    clicks.put("basis", clicks.getInt("basis") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis", clicks.getInt("basis") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis", counts.getInt("basis") + 1);
            }
            case "basis-plus" -> {
                if(profileViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("basis-plus", clicks.getInt("basis-plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("basis-plus", counts.getInt("basis-plus") + 1);
            }
            case "plus" -> {
                if(profileViews) {
                    clicks.put("plus", clicks.getInt("plus") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("plus", clicks.getInt("plus") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("plus", counts.getInt("plus") + 1);
            }
            case "premium" -> {
                if(profileViews) {
                    clicks.put("premium", clicks.getInt("premium") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("premium", clicks.getInt("premium") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("premium", counts.getInt("premium") + 1);
            }
            case "sponsor" -> {
                if(profileViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + userStatsRepository.findByUserId(u.getId()).getProfileView());
                }
                if(postViews) {
                    clicks.put("sponsor", clicks.getInt("sponsor") + getClickTotalOnPostsOfUser(Math.toIntExact(u.getId())));
                }
                counts.put("sponsor", counts.getInt("sponsor") + 1);
            }
        }
    }

    private void buildAveragesFromCountsAndClicks(JSONObject counts, JSONObject clicks, JSONObject averages) throws JSONException {
        if(counts.getInt("basis") != 0) {
            averages.put("basis", clicks.getInt("basis") / counts.getInt("basis"));
        } else {
            averages.put("basis", 0);
        }
        if(counts.getInt("basis-plus") != 0) {
            averages.put("basis-plus", clicks.getInt("basis-plus") / counts.getInt("basis-plus"));
        } else {
            averages.put("basis-plus", 0);
        }
        if(counts.getInt("plus") != 0) {
            averages.put("plus", clicks.getInt("plus") / counts.getInt("plus"));
        } else {
            averages.put("plus", 0);
        }
        if(counts.getInt("premium") != 0) {
            averages.put("premium", clicks.getInt("premium") / counts.getInt("premium"));
        } else {
            averages.put("premium", 0);
        }
    }

    /**
     * Fetch all clicks on posts of this user.
     * @param uid the authors id to fetch for.
     * @return total amount of clicks on this user's posts.
     */
    public int getClickTotalOnPostsOfUser (int uid){
        List<Post> posts= postRepo.findByAuthor(uid);
        int clicks = 0;
        for(Post post : posts) {
            if(post != null) {
                clicks += statRepository.getSumClicks(post.getId()) != null ? statRepository.getSumClicks(post.getId()) : 0;
            }
        }

        return clicks;
    }

    /**
     * Fetches a JSON for all Users in Email-List.
     * @return a JSON-String containing data on all mails to users.
     * @throws JSONException .
     */
    public String getJSONForEmailListAll() throws JSONException {
        JSONArray array = new JSONArray();
        for(StatMails mail : statMailsRepo.findAll()) {
            JSONObject json = new JSONObject();

            json.put("id", mail.getUserId());
            json.put("content", !mail.getContent().isBlank());
            json.put("sent", mail.getSent() == 1);

            array.put(json);
        }

        return array.toString();
    }

    /**
     * Method finds all dates user had views in, and adds the date and the views on that day into one list each.
     * @param userId the id of the user you are fetching for.
     * @return a JSON-String of a JSON-Object containing JSON-Array-Strings under the labels "dates" and "views".
     * @throws JSONException .
     */
    public String getProfileViewsByTime(Long userId) throws JSONException {
        JSONArray dates = new JSONArray();
        JSONArray views = new JSONArray();

        if(userViewsRepo.existsByUserId(userId)) {
            List<Integer> userViewDays = userViewsRepo.getUniIdsForUser(userId);
            for(Integer uniId : userViewDays) {
                if(uniRepo.findById(uniId).isPresent()) {
                    dates.put(uniRepo.findById(uniId).get().getDatum().toString().substring(0, 9));
                    views.put(userViewsRepo.getSumByUniIdAndUserId(uniId, userId));
                }
            }

        } else {
            return "No Views found for user.";
        }

        return new JSONObject().put("dates", dates.toString()).put("views", views.toString()).toString();
    }

    /**
     * This accounts for users with and without posts, but does count post-views towards their averages. Hence, users with posts will seem better here.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type.
     * @throws JSONException .
     */
    public String getUserAveragesWithPostClicks() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);

        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            addCountAndProfileViewsByType(counts, clicks, u, stats, hasPost(Math.toIntExact(u.getId())));
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages.toString();
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their profile views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    public String getUserAveragesWithPostsWithoutPostClicksDebug() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            boolean stats = userStatsRepository.existsByUserId(u.getId());
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, stats);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages + " ProfileViews";
    }

    /**
     * This accounts for ONLY users that have posts, counting ONLY their post's views.
     * @return a JSON-String containing averages of profile-views keyed by their Account-Type. With a debug-label.
     * @throws JSONException .
     */
    public String getUserAveragesWithPostsOnlyPostClicksDebug() throws JSONException {
        JSONObject counts = new JSONObject();
        JSONObject clicks = new JSONObject();
        JSONObject averages = new JSONObject();

        counts.put("basis", 0);
        counts.put("basis-plus", 0);
        counts.put("plus", 0);
        counts.put("premium", 0);
        counts.put("sponsor", 0);

        clicks.put("basis", 0);
        clicks.put("basis-plus", 0);
        clicks.put("plus", 0);
        clicks.put("premium", 0);
        clicks.put("sponsor", 0);
        for(WPUser u : userRepo.findAll()) {
            if(hasPost(Math.toIntExact(u.getId()))) {
                addCountAndProfileViewsByType(counts, clicks, u, false, true);
            }
        }
        buildAveragesFromCountsAndClicks(counts, clicks, averages);
        return averages + " -PostClicks";
    }

    /**
     * Fetches the account types of all users.
     * @return a JSON-String containing the account types of all users as counts.
     */
    public String getAccountTypeAll(){
        HashMap<String, Integer> counts = new HashMap<>();

        for(WPUser user : userRepo.findAll()) {
            switch(this.getType(Math.toIntExact(user.getId()))) {
                case "admin" -> {
                    counts.put("Administrator", counts.get("Administrator") == null ? 1 : counts.get("Administrator") + 1);
                }
                case "basis" -> {
                    counts.put("Basic", counts.get("Basic") == null ? 1 : counts.get("Basic") + 1);
                }
                case "basis-plus" -> {
                    counts.put("Basic-Plus", counts.get("Basic-Plus") == null ? 1 : counts.get("Basic-Plus") + 1);
                }
                case "plus" -> {
                    counts.put("Plus", counts.get("Plus") == null ? 1 : counts.get("Plus") + 1);
                }
                case "premium" -> {
                    counts.put("Premium", counts.get("Premium") == null ? 1 : counts.get("Premium") + 1);
                }
                case "sponsor" -> {
                    counts.put("Sponsor", counts.get("Sponsor") == null ? 1 : counts.get("Sponsor") + 1);
                }
                case "none" -> {
                    counts.put("Anbieter", counts.get("Anbieter") == null ? 1 : counts.get("Anbieter") + 1);
                }
            }
        }
        return new JSONObject(counts).toString();
    }

    /**
     *
     * @return ein JSON-String, der die Anzahl der Accounts pro Account-Typ enthält.
     */
    public String getAccTypes() {
        HashMap<String, Long> map = new HashMap<>();
        UniversalStats uni = uniRepo.findAll().get(uniRepo.findAll().size() -2);

        map.put("Anbieter", uni.getAnbieter_abolos_anzahl());
        map.put("Basic", uni.getAnbieterBasicAnzahl());
        map.put("Basic-Plus", uni.getAnbieterBasicPlusAnzahl());
        map.put("Plus", uni.getAnbieterPlusAnzahl());
        map.put("Premium", uni.getAnbieterPremiumAnzahl());
        map.put("Sponsor", uni.getAnbieterPremiumSponsorenAnzahl());


        return new JSONObject(map).toString();
    }

    /**
     * Fetches a representation of all user-plan changes within the last week.
     * @return a JSON-Object containing Lists of Strings and counts of items for those lists.
     * @throws JSONException .
     */
    public String getNewUsersAll() throws JSONException {
        JSONObject obj = new JSONObject();

        Comparator<String> customComparator = Comparator.comparing(s -> s.charAt(0));

        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        List<String> ohne = new ArrayList<>(), basis = new ArrayList<>(), basis_plus = new ArrayList<>(), plus = new ArrayList<>(), premium = new ArrayList<>();

        for(WPUser user : userRepo.findAll()) {

            String secondLastMembership = memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).size() > 0 ? memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).get(0).getMembership() : "none";

            if(memberRepo.getLastByUserId(user.getId()) == null) continue;
            if(memberRepo.getLastByUserId(user.getId()).getTimestamp().toLocalDateTime().isAfter(lastWeek) && !getType(Math.toIntExact(user.getId())).equals("admin")) {
                char preSign = '+';
                if (memberRepo.getPageableSingle(user.getId(), PageRequest.of(1,1)).size() > 0 && !memberRepo.getLastByUserId(user.getId()).getMembership().equals("deleted")) {
                    preSign = '&';
                } else if (memberRepo.getLastByUserId(user.getId()).getMembership().equals("deleted")) {
                    preSign = '-';
                }

                List<String> newMembership, oldMembership;

                switch (memberRepo.getLastByUserId(user.getId()).getMembership()) {
                    case "none" -> {
                        newMembership = ohne;
                    }
                    case "basis" -> {
                        newMembership = basis;
                    }
                    case "basis-plus" -> {
                        newMembership = basis_plus;
                    }
                    case "plus" -> {
                        newMembership = plus;
                    }
                    case "premium" -> {
                        newMembership = premium;
                    }
                    default -> newMembership = null;
                }
                if (memberRepo.getPageableSingle(user.getId(), PageRequest.of(1, 1)).size() > 0) {
                    switch (secondLastMembership) {
                        case "none" -> {
                            oldMembership = ohne;
                        }
                        case "basis" -> {
                            oldMembership = basis;
                        }
                        case "basis-plus" -> {
                            oldMembership = basis_plus;
                        }
                        case "plus" -> {
                            oldMembership = plus;
                        }
                        case "premium" -> {
                            oldMembership = premium;
                        }
                        default -> oldMembership = null;
                    }
                } else {
                    oldMembership = null;
                }

                addToUserList(preSign, newMembership, oldMembership, user);
            }
        }

        ohne.sort(customComparator);
        basis.sort(customComparator);
        basis_plus.sort(customComparator);
        plus.sort(customComparator);
        premium.sort(customComparator);

        obj.put("ohne", new JSONArray(ohne));
        obj.put("basis", new JSONArray(basis));
        obj.put("basisPlus", new JSONArray(basis_plus));
        obj.put("plus", new JSONArray(plus));
        obj.put("premium", new JSONArray(premium));

        obj.put("ohneCount", getUserChangeCountFromList(ohne));
        obj.put("basisCount", getUserChangeCountFromList(basis));
        obj.put("basisPlusCount", getUserChangeCountFromList(basis_plus));
        obj.put("plusCount", getUserChangeCountFromList(plus));
        obj.put("premiumCount", getUserChangeCountFromList(premium));

        return obj.toString();

    }

    private void addToUserList(char preSign, List<String> newMembership, List<String> oldMembership, WPUser user) {

        switch(preSign) {
            case '&' -> {
                if(newMembership != null) newMembership.add("+" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
                if(oldMembership != null) oldMembership.add("-" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
            }
            case '+' -> {
                if(newMembership != null) newMembership.add("+" + user.getDisplayName() + "<&>" + getType(Math.toIntExact(user.getId())));
            }
            case '-' -> {
                if(oldMembership != null) oldMembership.add("-" + user.getDisplayName() + "<&>" + "DELETED");
            }
        }

    }

    private int getUserChangeCountFromList(List<String> list) {
        int total = 0;
        for(String user : list) {
            total = user.charAt(0) == '+' ? total + 1 : total - 1;
        }
        return total;
    }

    /**
     * Fetch the full log of changes in memberships of this user.
     * @param page the page of results.
     * @param size the amount of results.
     * @param userId the user to fetch for.
     * @return a  String representation of all membership changes.
     * @throws JSONException .
     */
    public String getFullLog(int page, int size, String userId) throws JSONException {

        JSONArray array = new JSONArray();

        Page<MembershipsBuffer> buffers;

        if(userId == null || !userId.isBlank()) {
            buffers = memberRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id")));
        } else {
            buffers = memberRepo.findAllForUser(Integer.parseInt(userId), PageRequest.of(page, size));
        }

        for (MembershipsBuffer buffer : buffers) {
            JSONObject obj = new JSONObject();
            obj.put("newPlan", buffer.getMembership());
            obj.put("oldPlan", memberRepo.getPreviousMembership(buffer.getUserId(), buffer.getId()) == null ? "none" : memberRepo.getPreviousMembership(buffer.getUserId(), buffer.getId()));
            obj.put("time", buffer.getTimestamp().toString());
            obj.put("user", userRepo.findById(buffer.getUserId()).isPresent() ? userRepo.findById(buffer.getUserId()).get().getDisplayName() : buffer.getUserId());
            array.put(obj);
        }


        return array.toString();
    }

    /**
     * Checks whether the user has posts in each specific post-type.
     * @param id the users' id.
     * @return a JSON-String containing whether each post-type has a post by this author.
     * @throws JSONException .
     */
    public String hasPostByType(int id) throws JSONException {
        JSONObject jsonTypes = new JSONObject();
        boolean news = false, artikel = false, blog = false, podcast = false, whitepaper= false;
        for(Post p : postRepo.findByAuthor(id)) {
            switch(postService.getType(p.getId())) {
                case "news" -> news = true;
                case "artikel" -> artikel = true;
                case "blog" -> blog = true;
                case "podcast" -> podcast = true;
                case "whitepaper" -> whitepaper = true;
            }
        }
        jsonTypes.put("news", news);
        jsonTypes.put("artikel", artikel);
        jsonTypes.put("blog", blog);
        jsonTypes.put("podcast", podcast);
        jsonTypes.put("whitepaper", whitepaper);

        return jsonTypes.toString();
    }

    /**
     * Fetch how much of the anbieterprofilvervollständigung has been fulfilled on average globally.
     * @return percentage.
     */
    public double getPotentialPercentGlobal(){
        List<WPUser> users = userRepo.findAll();
        int countUsers = users.size();
        double potentialPercentCollector = 0;
        for(WPUser user : users) {
            try {
                potentialPercentCollector+= getPotentialPercent(Math.toIntExact(user.getId()));
            } catch (JSONException ignored) {
            }
        }
        return potentialPercentCollector / countUsers;
    }

    /**
     * Fetch this user's rankings in content and profile by group and total.
     * @param id the users' id.
     * @return a JSON-String of this user's rankings in content and profile views by group and total.
     * @throws JSONException .
     */
    public String getRankings(long id) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("rankingContent", getRankingTotalContentViews(id));
        obj.put("rankingContentByGroup", getRankingInTypeContentViews(id));
        obj.put("rankingProfile", getRankingTotalProfileViews(id));
        obj.put("rankingProfileByGroup", getRankingInTypeProfileViews(id));
        return obj.toString();
    }

    /**
     * Gibt die verteilten Ansichten (Views) eines Benutzers über die letzten 24 Stunden als JSON-String zurück.
     * Die Methode berechnet die Ansichten basierend auf den Daten der letzten zwei Tage (basierend auf uniId)
     * für den angegebenen Benutzer (userId). Für jede Stunde der letzten 24 Stunden werden die Ansichten ermittelt.
     * Falls für eine bestimmte Stunde keine Daten vorhanden sind, wird der Wert 0 angenommen.
     *
     * @param userId   Die ID des Benutzers, für den die Ansichten abgerufen werden sollen.
     * @param daysback Gibt an, wie viele Tage zurückliegend die Daten berücksichtigt werden sollen.
     *                 Ein Wert von 0 bedeutet, dass die Daten für heute und gestern berücksichtigt werden.
     * @return Ein JSON-String, der eine Map darstellt, wobei jeder Schlüssel eine Stunde (0-23) und jeder Wert
     *         die Anzahl der Ansichten (Views) für diese Stunde ist. Das Format ist {"Stunde": Ansichten, ...}.
     * @throws JsonProcessingException Wenn beim Verarbeiten der Daten zu einem JSON-String ein Fehler auftritt.
     */
    public String getUserViewsDistributedByHours(@RequestParam Long userId,@RequestParam int daysback) throws JsonProcessingException {
        int latestUniId = uniRepo.getLatestUniStat().getId() - daysback;
        int previousUniId = latestUniId - 1;
        System.out.println("latestUniId: "+latestUniId+'\n'+"previousUniId: " + previousUniId);
        List<UserViewsByHourDLC> combinedViews = new ArrayList<>();
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, previousUniId)); // Daten von gestern
        combinedViews.addAll(userViewsRepo.findByUserIdAndUniId(userId, latestUniId));   // Daten von heute
        System.out.println("combined views: "+combinedViews);
        Map<Integer, Long> hourlyViews = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();

        for (int i = 23; i >= 0; i--) {
            int hour = (currentHour - i + 24) % 24;
            Long viewCount = combinedViews.stream()
                    .filter(view -> view.getHour() == hour)
                    .map(UserViewsByHourDLC::getViews)
                    .findFirst()
                    .orElse(0L);
            hourlyViews.put(hour, viewCount);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(hourlyViews);
    }

    /**
     *
     * @param cryptedTag a full value of a profiles tags, including MULTIPLE tags.
     * @return a List of Strings, containing all Tags in this String.
     */
    public List<String> decryptTags(String cryptedTag) {
        Pattern cleaner = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = cleaner.matcher(cryptedTag);
        List<String> tags = new ArrayList<>();
        while(matcher.find()) {
            tags.add(matcher.group(1));
        }
        return tags;
    }

    /**
     *
     * @param cryptedTags profile_tags String for several users.
     * @return a List of a List of Strings, containing all Tags in this String.
     */
    public List<List<String>> decryptTagsStringInList(List<String> cryptedTags) {
        List<List<String>> list = new ArrayList<>();
        for(String tags : cryptedTags) {
            list.add(decryptTags(tags));
        }
        return list;
    }

    /**
     * Fetch all tags for a specific user, set for a specific membership.
     * @param userId the users' id.
     * @param type the membership to fetch for.
     * @return a php-array of all tags the user has set for this membership.
     */
    public Optional<String> getTags(long userId, String type) {
        switch (type) {
            case "basis" -> {
                return wpUserMetaRepository.getTagsBasis(userId);
            }
            case "basis_plus" -> {
                return wpUserMetaRepository.getTagsBasisPlus(userId);
            }
            case "plus" -> {
                return wpUserMetaRepository.getTagsPlus(userId);
            }
            case "premium" -> {
                return wpUserMetaRepository.getTagsPremium(userId);
            }
        }
        return Optional.empty();
    }

    /**
     * Fetch all users' ids that have set tags.
     * @return a list of user ids.
     */
    public List<Long> getAllUserIdsWithTags() {
        List<Long> list = new ArrayList<>();
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsBasis());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsBasisPlus());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsPlus());
        list.addAll(wpUserMetaRepository.getAllUserIdsWithTagsPremium());
        return list;
    }

    /**
     * Fetch how many users have set each tag.
     * @return a JSON-Object mapping tags to their counts.
     * @throws JSONException .
     */
    public JSONObject getUserCountForAllTags() throws JSONException {
        List<String> allTags = getAllUserTagRowsInList(getAllUserIdsWithTags());
        List<List<String>> decryptedAndCleanedTags= decryptTagsStringInList(allTags);
        JSONObject json = new JSONObject();

        for(List<String> tags : decryptedAndCleanedTags) {
            for (String tag : tags) {
                try {
                    json.put(tag, json.getInt(tag) + 1);
                } catch (Exception e) {
                    json.put(tag, 0);
                }
            }
        }

        return json;
    }

    /**
     * Fetches all User-Tags for all memberships, for all users in list.
     * @param list a list of user-ids.
     * @return a list of all tags of all users in list.
     */
    public List<String> getAllUserTagRowsInList(List<Long> list) {
        List<String> listOfTags = new ArrayList<>();
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListBasis(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListBasisPlus(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListPlus(list));
        listOfTags.addAll(wpUserMetaRepository.getAllUserTagRowsInListPremium(list));
        return listOfTags;
    }

    /**
     * Berechnet den prozentualen Anteil der Anbieter für alle Tags.
     *
     * @return Eine Map von Tags zu ihrem jeweiligen prozentualen Anteil an der Gesamtzahl der Benutzer.
     */
    public JSONArray getUserCountForAllTagsInPercentage() throws JSONException {
        // Gesamtzahl der Benutzer mit mindestens einem Tag ermitteln
        int totalUsersWithTag = getTotalCountOfUsersWithTags();

        // Tags und ihre Anzahl holen
        JSONObject companiesPerTag = getUserCountForAllTags();

        //Array als Container erstellen
        List<JSONObject> array = new ArrayList<>();

        // Map für prozentualen Anteil erstellen
        List<String> tagLabel = new ArrayList<>();
        List<Double> tagPercentages = new ArrayList<>();


        var iterator = companiesPerTag.keys();
        // Prozentualen Anteil für jeden Tag berechnen
        while(iterator.hasNext()) {
            String key = iterator.next().toString();
            int count = companiesPerTag.getInt(key);
            double percentage = (double) count / totalUsersWithTag * 100;
            array.add(new JSONObject().put(key, percentage));
        }

        array.sort((o1, o2) -> {
            try {
                double v = o2.getDouble(o2.keys().next().toString()) - o1.getDouble(o1.keys().next().toString());
                return (int) v;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return new JSONArray(array);
    }

    /**
     * Fetch all tags data for all users.
     * @return a JSON-String containing all tag-data of user-profiles.
     * @throws JSONException .
     */
    public String getAllUserTagsDataFusion() throws JSONException {
        JSONObject json = getUserCountForAllTags();
        var jsonKeys = json.keys();
        JSONArray array = new JSONArray();
        while(jsonKeys.hasNext()) {
            String tag = jsonKeys.next().toString();
            array.put(new JSONObject().put("count", json.getInt(tag)).put("name", tag));
        }
        array.put(new JSONObject().put("count", getAllUserTagRowsInList(getAllUserIdsWithTags()).size()).put("name", "countTotal"));
        return array.toString();
    }

    /**
     * Updates the user-rankings buffer table.
     * @return whether the update was successful.
     */
    public boolean updateUserRankingBuffer() {
        try {
            updateRanksTotal();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            updateRankGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates all rankings in group (as opposed to total).
     */
    public void updateRankGroups() {

        //Reset all
        if(!rankingGroupContentRepo.findAll().isEmpty()) {
            rankingGroupContentRepo.deleteAll();
        }
        if(!rankingGroupProfileRepo.findAll().isEmpty()) {
            rankingGroupProfileRepo.deleteAll();
        }

        for(String type : Constants.getInstance().getListOfUserTypesDirty()) {
            int rank = 1;
            List<WPUser> users = userRepo.getByAboType(type);

            //Sort for profile-view rankings
            users.sort((o1, o2) -> Math.toIntExact((userStatsRepository.existsByUserId(o2.getId()) ? userStatsRepository.findByUserId(o2.getId()).getProfileView() : 0) - (userStatsRepository.existsByUserId(o1.getId()) ? userStatsRepository.findByUserId(o1.getId()).getProfileView() : 0)));
            //Make entries for profile-view rankings.
            for(WPUser user : users) {
                RankingGroupProfile profileRank = new RankingGroupProfile();
                profileRank.setRank(rank);
                rank++;
                profileRank.setType(type);
                profileRank.setUserId(Math.toIntExact(user.getId()));
                rankingGroupProfileRepo.save(profileRank);
            }
            rank = 1;

            //Sort for content-view rankings
            users.sort((o1, o2) -> Math.toIntExact(postService.getPostViewsOfUserById(o2.getId()) - postService.getPostViewsOfUserById(o1.getId())));
            //Make entries for content-view rankings.
            for(WPUser user : users) {
                RankingGroupContent contentRank = new RankingGroupContent();
                contentRank.setRank(rank);
                rank++;
                contentRank.setType(type);
                contentRank.setUserId(Math.toIntExact(user.getId()));
                rankingGroupContentRepo.save(contentRank);
            }

        }
    }

    /**
     * Updates all rankings in total (as opposed to group).
     */
    public void updateRanksTotal() {
        if(!rankingTotalContentRepo.findAll().isEmpty()) {
            rankingTotalContentRepo.deleteAll();
        }
        if(!rankingTotalProfileRepo.findAll().isEmpty()) {
            rankingTotalProfileRepo.deleteAll();
        }
        int rank = 1;

        List<WPUser> users = userRepo.getAllWithAbo();
        //Sort for profile-view rankings
        users.sort((o1, o2) -> Math.toIntExact((userStatsRepository.existsByUserId(o2.getId()) ? userStatsRepository.findByUserId(o2.getId()).getProfileView() : 0) - (userStatsRepository.existsByUserId(o1.getId()) ? userStatsRepository.findByUserId(o1.getId()).getProfileView() : 0)));
        //Make entries for profile-view rankings.
        for(WPUser user : users) {
            RankingTotalProfile profileRank = new RankingTotalProfile();
            profileRank.setRank(rank);
            rank++;
            profileRank.setUserId(Math.toIntExact(user.getId()));
            rankingTotalProfileRepo.save(profileRank);
        }
        rank = 1;

        //Sort for content-view rankings
        users.sort((o1, o2) -> Math.toIntExact(postService.getPostViewsOfUserById(o2.getId()) - postService.getPostViewsOfUserById(o1.getId())));
        //Make entries for content-view rankings.
        for(WPUser user : users) {
            RankingTotalContent contentRank = new RankingTotalContent();
            contentRank.setRank(rank);
            rank++;
            contentRank.setUserId(Math.toIntExact(user.getId()));
            rankingTotalContentRepo.save(contentRank);
        }
    }

    /**
     * Fetches the average redirects by profile membership.
     * @return JSON-String containing the average redirects by profile membership.
     * @throws JSONException .
     */
    public String getAverageRedirectsByPlan() throws JSONException {
        JSONObject obj = new JSONObject();
        for(String type : Constants.getInstance().getListOfUserTypesDirty()) {
            List<WPUser> users = userRepo.getByAboType(type);
            List<Long> userIds = new ArrayList<>();
            for(WPUser user : users) {
                userIds.add(user.getId());
            }

            obj.put(type.replace("um_", ""), userRedirectsRepo.getSumRedirectsOfUsersInList(userIds) == null ? 0 :  userRedirectsRepo.getSumRedirectsOfUsersInList(userIds) / userIds.size());
        }

        return obj.toString();
    }

    /**
     * Fetches all anbieter names that contain the given search.
     * @param search .
     * @return a JSON-Array-String of Anbieter-names.
     */
    public String getUsernamesByStart(String search, String abo, String typ) {return new JSONArray(userRepo.getUsernamesBySearch(search, abo, typ)).toString();}
}

