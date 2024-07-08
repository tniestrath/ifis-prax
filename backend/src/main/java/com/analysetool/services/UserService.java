package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.modells.StatMails;
import com.analysetool.modells.UserStats;
import com.analysetool.modells.UserViewsByHourDLC;
import com.analysetool.modells.WPUser;
import com.analysetool.repositories.*;
import com.analysetool.util.Constants;
import com.analysetool.util.DashConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private PostController postController;
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

    private final DashConfig config;

    private String imageProfile = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAIASURBVFiFvdY/a5RBEMfxz5MLRIwRFRtRAioIgn8CsbKwM2KnFhEbCytfhJ2IlWCjIIgWCmqrBHwBFiKEWFgoGBSxSKGFaKLBxLG4S1juLnf73D2XgYFnH57Z33efnZndIiKUsaIoTuIiJnAc3zCHN7gbEYulJoyILMcW3MIqYgOfx6ncOSMiDwAjmO0gnPo/XK4a4Gam+Jr/wHglADiBlZIAgZc5AEMZaXIOtcyUSm2qKIqxbh/lAEz0IA6FepX0DXC0R4Cs2ByAhT4AusbmAMz1AdA1NgfgdY/iCxHxuetXGWVYU2+zZctwuspGdBi/S4g/q7QTNiCm8DVD/DG2Vw7QgNiBh1hqIzyP82XmiwhFY+K2VhTFfkziIGYi4l3jfQ2HJMdxRHxP4iZxGh8wGxFfSiUhduKe+smWrvIBdnf4Q3vwtClmBbcxlrUF6uf+2w57vIT7mMYRHMMlPMJyh7hXGM4BuNMlyfrxGx0BsA9/BwiwiF2djuOrGG6bLNXYVlxJXzQDnBmgeHuN5PeP6u3mU9Z/YajdFhzQ282nrI1i79ogBRjfBPEWrRRg2yYCrGulADl3g6psqOUB7zcR4OP6U1IFI/hj8FXwE7WWKoiIZVwf0IpTuxYRq+ujplY8jBc9rCrXn0h6wIYXElzADD5pPZLL+Kr6ReU5zrbT+g/lp72yFoh52gAAAABJRU5ErkJggg==";
    private String imageContent = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAACXBIWXMAABFNAAARTQHAOWBjAAAB/0lEQVRIiWL4//8/xZiBgcGAgYHhAQMDA4gDwgcYGBgEwGb//88AAAAA//+ilgUfkCyA4Qtgi/7/ZwAAAAD//6KVBQiLGBgEAAAAAP//otSSCXgsgGAGhgMAAAAA//+ixAcToOwFeC1hYPgPAAAA//+iNIgWELSIgeE/AAAA//+iRhzgt4iB4QAAAAD//6JWJOOy6AMDA4MBAAAA//8i1wIQOwCaenBZBLbg////DAAAAAD//yLXApAYvqADpTqwBf///2cAAAAA//+ipgUwDE51cPz/PwMAAAD//6K2BfAgguP//xkAAAAA//+iuQX///9nAAAAAP//orkF////ZwAAAAD//6K5Bf///2cAAAAA//8ilA8aKLXg////DAAAAAD//0K2BFQHECrsSLbg////DAAAAAD//4JZkIBWPCNXQBRZ8P//fwYAAAAA//+CWYJcHCgQ8BVJFvz//58BAAAA//+CWQIL8wtI8QOyCN1HJFvw//9/BgAAAAD//wIZ6IAtt0J9hFw2kWXB////GQAAAAD//2KBFnQw8IGRkRFU7oDE5JHEP4Ic8///f5ClpAEGBgYAAAAA//8CYWTXYq8PGBgUyPEBGP//zwAAAAD//wJZgityQYkB5HqyDQfj//8ZAAAAAP//Qs4fIB+BMx81DIbj//8ZAAAAAP//AwARtZXpaOI3DgAAAABJRU5ErkJggg==";
    private String imageDaily = "iVBORw0KGgoAAAANSUhEUgAAABkAAAAZCAYAAADE6YVjAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAJYSURBVEhLvZbNS1ZBFIevhRYq2BcE0sYKJALXFS2Lighatq+V0K7+CAP/CME/oIWgEAVtCzLcRRD0qdEHRZuorJ7nOkeO876YmvWDh/vemXPOnTlzZubtadbXEFwojMEh2A8f4BUswCzMwSfYlHbBTXgBvzaAdtrrtyEdh4cQAZ7ABJyB66XNp++22x+2+um/rk7De9DhDVyF3RA6Ad/LM2S/dtrrp79xusoRxAfug/mvtROOlGct7fWLD3XMyFxGijQcgK1Iv/iQ8daskYtmh1PuNoPNSP9InXFb7YGXYKO5rTUI9cxyunaUZ5ZxjGfVuQ2aK6XBKsmLrPM0OCqNb8MBOAiPwMrS/i5ol/ec7VF1xm+myotOWX2QS1km4WT5/RhcXH+7Oc1IlvHsm3K07mR1pzxD3+AcHIbI7VGI9Nj/FX6AZV0r4o3pEAt9DKz/nO+PMAyn2reVgI5OOXI3pDGiTelvHOOpNv5P0EgckfsgNA7RJy6oa/IltYnrFunS3zjRtwzNu/LiUZFnYo0/Bftc2IsQugT3wAOy/kjMJI4g4zfz5cWpZ+2Ft+BMz8MI5OpTo2AKl6BeeOMZd958Ohp1tjyzHJWl6XH+DKyuWtr0rvxco4i34EcMoC5DHqn3wwyYiuewCH4o6zWYNmN8tqHIOMZTbXx3ZNwb3Xa8u13627dORUlnXQPjre54dQNsdNTbeXYZd1X/5RRW//w+CdU3o7nNxWD9/9XNGHIED0AH8TS9BZbkttzxIXPpoRhV9ye0075jDdRW/nftAw/O/L+r3idJTfMbUfvNNr3flUgAAAAASUVORK5CYII=";
    private String imageRedirect = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAALPSURBVFhH1ZfL6w1RHMCvZwhRFBbkLWEjr9hIoSjPlNfSwubHgqWNWPkDSMpCLBTlsfDIjrxKIiulEAsboizx+cycuc0998zv3hkkn/p0z8w553vOPXMeM63/nQPBxgwNv02ZEWzMsPBbh5m4AbfhJpyDU3AafsfP2Dd1RmA73sWbuA9H47fgKNyLN9Aylv1jzMd7+BRteAIWHA8WmGeZJ2gd6/4Wm/EdHsUh3og4EoyxrHWsa4xGWPE9rs2u0owMVmFdY9TuhENn7wdrXHp1QIxhrFqPw+fnEFYxCc/i86DpyViFsYzZF85gJ1zqmctYfIxncEHQtPfMS2EsY7p0e+IyciZXcQiv5ckOvHc4TybZj8buIN4H3GTcUFzrVSxH13uM95blySTmG9s22sQdMPgb/JJdpfmEs/NkB7PQvCqMaWzbaBN3YB6+yJOVnMMtuCK7yjG9Fc0bjJc4N0/mODnsxB6ciLvQnt5B9/RL+APLOIxXcCXe9gZ4Nrj7OYE/eKNEOf56dLe8jFl8M9XnshAt5HIy7b14hHbgA7Sx3XghaPoR3sedWKYc39h2oCp+6xiezJNdLMK3uDq7SmOeZRZnV90Y2zbaxD14jUvyZBcuv/PoCFRhnmUsm8LYtlGJM/kVlk+8Aod3TZ4cFEfBsjHGNLZttIlHwGXyEePDo5isvnD0wjKWjXdSYxrbNtp0TQI4jQMYBxiO03Fq+E1Z5I3AMsbysRi7g7iRAg+OW3gqu8q5iEvRf1hV7yeOwWfo0ivwMNqI67KrPkgdx/6r8ZHjgvH98gg0Oo6lnxeSXli30QtJgRXtvUOYGvaq7wLLWse6jRsvcOiKl1KP1PISPREsMM+j3LJ9vZRWTaYU7vMH0bPApeShtQrlIbrJuMZdas72q9iTOh0ocA/3SPVU81SU6+gO5xnRsc7/NvF3QW1SG1Edvgb/GX6SaUNarV8yHYTxxkCURwAAAABJRU5ErkJggg==";

    public UserService(DashConfig config) {
        this.config = config;
    }






    private final String tableBase = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Analyse ihrer Marktplatz Präsens</title>\n" +
            "<style>\n" +
            "        html, body{\n" +
            "            width: 100%;\n" +
            "            display: flex;\n" +
            "            flex-direction: column;\n" +
            "            align-items: center;\n" +
            "            background: #cccccc;\n" +
            "        }\n" +
            "        .chapter{\n" +
            "            width: 95%;\n" +
            "            background: white;\n" +
            "            display: flex;\n" +
            "            flex-direction: column;\n" +
            "            align-items: center;\n" +
            "        }\n" +
            "        .chapter-title{\n" +
            "            text-align: center;\n" +
            "            font-size: x-large;\n" +
            "        }\n" +
            "        ul{\n" +
            "            list-style: none;\n" +
            "            padding-left: 0;\n" +
            "            min-width: 50%;\n" +
            "        }\n" +
            "        ul ul{\n" +
            "            padding-left: 40px;\n" +
            "          \n" +
            "        }\n" +
            "        li{\n" +
            "            list-style: none;\n" +
            "        }\n" +
            "        img{\n" +
            "            height: 1em;\n" +
            "            width: auto;\n" +
            "            aspect-ratio: 1/1;\n" +
            "        }\n" +
            "        table{\n" +
            "            border-collapse: collapse;\n" +
            "        }\n" +
            "        tr{\n" +
            "            border-bottom: 1px solid #ddd;\n" +
            "        }\n" +
            "        .rankings-2, .rankings-3{\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "    </style>" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"quarterStats\" class=\"chapter\">\n" +
            "        <p class=\"chapter-title\">Ihr Zuwachs im letzten Quartal:</p>\n" +
            "        <ul id=\"quarterStatsList\">\n" +
            "            <li id=\"qs-1\">Profilaufrufe: {{PROFILEVIEWSQUARTER}}<img src=\"data:image/png;base64,{{IMAGEPROFILE}}\"/><br>\n" +
            "            <li id=\"qs-2\">Weiterleitungen zur eigenen Homepage: {{REDIRECTSQUARTER}}<img src=\"data:image/png;base64,{{IMAGEREDIRECTS}}\"/></li>\n" +
            "            <li id=\"qs-3\">Inhaltsaufrufe: {{CONTENTVIEWSQUARTER}}<img src=\"data:image/png;base64,{{IMAGECONTENT}}\"/></li>\n" +
            "        </ul>\n" +
            "    </div>\n" +
            "    <div id=\"baseStats\" class=\"chapter\">\n" +
            "        <p class=\"chapter-title\">Ihre Gesamtübersicht:</p>\n" +
            "        <ul id=\"baseStatsList\">\n" +
            "            <li id=\"bs-1\">Profilaufrufe: {{PROFILEVIEWS}}<img src=\"data:image/png;base64,{{IMAGEPROFILE}}\"/><br>\n" +
            "                <ul>\n" +
            "                    <li id=\"bs-1-1\">Tägliche Aufrufe: {{DAILYVIEWS}}<img src=\"data:image/png;base64,{{IMAGEDAILY}}\"/></li>\n" +
            "                    <li id=\"bs-1-2\">Weiterleitungen zur eigenen Homepage: {{REDIRECTS}}<img src=\"data:image/png;base64,{{IMAGEREDIRECTS}}\"/></li>\n" +
            "                </ul>\n" +
            "            </li>\n" +
            "            <li id=\"bs-2\">Inhaltsaufrufe: {{CONTENTVIEWS}}<img src=\"data:image/png;base64,{{IMAGECONTENT}}\"/></li>\n" +
            "        </ul>\n" +
            "    </div>\n" +
            "    <div id=\"rankings\" class=\"chapter\">\n" +
            "        <ul id=\"rankingsList\">\n" +
            "            <li id=\"rs-1\">Ihre Platzierung nach Profilaufrufen: #{{PROFILERANK}}<br>\n" +
            "                Innerhalb des gleichen Packets: #{{GROUPPROFILERANK}}</li><br>\n" +
            "            <li id=\"rs-2\">Ihre Platzierung nach Inhaltsaufrufen: #{{CONTENTRANK}}<br>\n" +
            "                Innerhalb des gleichen Packets: #{{GROUPCONTENTRANK}}</li>\n" +
            "        </ul>\n" +
            "        <table id=\"rankingsTable\">\n" +
            "            <thead>\n" +
            "            <tr>\n" +
            "                <th>Gewählte Themen</th>\n" +
            "                <th>Ihre Platzierung</th>\n" +
            "                <th>Globale Nutzungshäufigkeit</th>\n" +
            "            </tr>\n" +
            "            </thead>\n" +
            "            <tbody>\n" +
            "            {{TABLEROW}}\n" +
            "            </tbody>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "\n" +
            "</body>\n" +
            "</html>";

    private final String tablerowBase = "<tr><td class='rankings-1'>REPLACE1</td><td class='rankings-2'>REPLACE2</td><td class='rankings-3'>REPLACE3</td></tr>";

    @Scheduled(cron = "0 0 0 1 */3 ?")
    public void generateMails() throws JSONException {
        generateMailsPlus();
        generateMailsPremium();
    }

    private void generateMailsPremium() {
        for(WPUser user : userRepo.getByAboType("premium")) {

        }
    }

    private void generateMailsPlus() throws JSONException {
        for(WPUser user : userRepo.getByAboType("plus")) {
            generateMailSingle(Math.toIntExact(user.getId()));
        }
    }

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

        //Add Ranking Table Data
        String content = tableBase.replace("{{TABLEROW}}",  makeRankingTable("plus", Math.toIntExact(user.getId())))
                .replace("{{PROFILEVIEWS}}", obj.getString("profileViews"))
                .replace("{{DAILYVIEWS}}", obj.getString("viewsPerDay"))
                .replace("{{REDIRECTS}}", obj.getString("redirects"))
                .replace("{{CONTENTVIEWS}}", obj.getString("postViews"))
                .replace("{{PROFILERANK}}", obj.getString("rankingProfile"))
                .replace("{{GROUPPROFILERANK}}", obj.getString("rankingProfileByGroup"))
                .replace("{{CONTENTRANK}}", obj.getString("rankingContent"))
                .replace("{{GROUPCONTENTRANK}}", obj.getString("rankingContentByGroup"))
                .replace("{{PROFILEVIEWSQUARTER}}", ((userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userViewsRepo.getSumForUserThisQuarter(userId) - userViewsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userViewsRepo.getSumForUserThisQuarter(userId) + " - " + userViewsRepo.getSumForUserPreviousQuarter(userId) + ")"))
                .replace("{{REDIRECTSQUARTER}}", (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (userRedirectsRepo.getSumForUserThisQuarter(userId) - userRedirectsRepo.getSumForUserPreviousQuarter(userId)) + "(" + userRedirectsRepo.getSumForUserThisQuarter(userId) + " - " + userRedirectsRepo.getSumForUserPreviousQuarter(userId) + ")")
                .replace("{{CONTENTVIEWSQUARTER}}", (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId) > 0 ? "+" : "") + (postClicksRepo.getSumForUserThisQuarter(userId) - postClicksRepo.getSumForUserPreviousQuarter(userId)) + "(" + postClicksRepo.getSumForUserThisQuarter(userId) + " - " + postClicksRepo.getSumForUserPreviousQuarter(userId) + ")")
                .replace("{{IMAGEDAILY}}", imageDaily)
                .replace("{{IMAGEPROFILE}}", imageProfile)
                .replace("{{IMAGEREDIRECTS}}", imageRedirect)
                .replace("{{IMAGECONTENT}}", imageContent)
                ;



        statMail.setContent(content);
        statMail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));

        statMailsRepo.save(statMail);
    }


    private String makeRankingTable(String accType, int userId) throws JSONException {
        StringBuilder content = new StringBuilder();

        JSONArray array = new JSONArray(getSingleUserTagsData(userId, "profile"));

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String row = tablerowBase;
            row = row.replace("REPLACE1", obj.getString("name")).replace("REPLACE2", obj.getString("ranking")).replace("REPLACE3", obj.getString("count"));
            content.append(row);
        }

        return content.toString();
    }

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
                    postController.getPostViewsOfUserById(userRepo.findByDisplayName(o2).get().getId()) : 0)
                    - (userRepo.findByDisplayName(o1).isPresent() ?
                    postController.getPostViewsOfUserById(userRepo.findByDisplayName(o1).get().getId()) : 0)));

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

    public String getTypeProfileTags(int id) {
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

    public List<List<String>> decryptTagsStringInList(List<String> cryptedTags) {
        List<List<String>> list = new ArrayList<>();
        for(String tags : cryptedTags) {
            list.add(decryptTags(tags));
        }
        return list;
    }

    public List<String> decryptTags(String cryptedTag) {
        Pattern cleaner = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = cleaner.matcher(cryptedTag);
        List<String> tags = new ArrayList<>();
        while(matcher.find()) {
            tags.add(matcher.group(1));
        }
        return tags;
    }

    public double getUserCountAsPercentageForSingleTag(String tag) {
        int totalUsersWithTag = getTotalCountOfUsersWithTags();
        int countForTag = countUsersByTag(tag);

        if (totalUsersWithTag == 0) {
            return 0; // Vermeidung der Division durch null
        }

        return (double) countForTag / totalUsersWithTag * 100;
    }

    public List<Long> getUserIdsByTag(String tag) {
        List<Long> list = new ArrayList<>();
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasis("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagBasisPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPlus("\"" + tag + "\""));
        list.addAll(wpUserMetaRepository.getUserIdsByTagPremium("\"" + tag + "\""));
        return list;
    }

    public Integer getTotalCountOfUsersWithTags() {
        return wpUserMetaRepository.getTotalCountOfUsersWithTagBasis() + wpUserMetaRepository.getTotalCountOfUsersWithTagBasisPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPlus() + wpUserMetaRepository.getTotalCountOfUsersWithTagPremium();
    }

    public Integer countUsersByTag(String tag) {
        return wpUserMetaRepository.countUsersByTagBasis("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagBasisPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPlus("\"" + tag + "\"") + wpUserMetaRepository.countUsersByTagPremium("\"" + tag + "\"");
    }


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
                obj.put("postViews", postController.getPostViewsOfUserById(user.getId()));
                obj.put("postCount", postController.getPostCountOfUserById(user.getId()));
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


    public double getPotentialPercent(int userId) throws JSONException {
        JSONObject json = new JSONObject(getPotentialByID(userId));

        int countFulfilled = 0; int countPossible = 0;
        countPossible+= 1 + 1 + 1 + 1 + json.getInt("tagsMax") + 1 + json.getInt("contactPublicMax") + json.getInt("contactInternMax") + json.getInt("companyDetailsMax") + json.getInt("solutionsMax");
        countFulfilled += json.getInt("profilePicture")
                + json.getInt("titlePicture") + json.getInt("bio") + json.getInt("slogan")
                + json.getInt("tagsCount") + json.getInt("contactPublic")
                + json.getInt("contactIntern") + json.getInt("companyDetails") + json.getInt("solutions");

        return (double) countFulfilled / countPossible;
    }

    public String getType(int id) {
        if (wpUserMetaRepository.existsByUserId((long) id)){
            String wpUserMeta = wpUserMetaRepository.getWPUserMetaValueByUserId((long) id);
            if (wpUserMeta.contains("customer")) return "none";
            if (wpUserMeta.contains("administrator")) return "admin";
            if (wpUserMeta.contains(Constants.getInstance().getPlusAnbieter())) return "plus";
            if (wpUserMeta.contains(Constants.getInstance().getBasisPlusAnbieter())) return "basis-plus";
            if (wpUserMeta.contains(Constants.getInstance().getPremiumAnbieter())) return "premium";
            if(wpUserMeta.contains(Constants.getInstance().getBasisAnbieter())) return "basis";
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

    public int getRankingInTypeProfileViews(long id) {
        return rankingGroupProfileRepo.getRankById(id).isPresent() ? rankingGroupProfileRepo.getRankById(id).get() : -1;
    }

    public int getRankingInTypeContentViews(long id) {
        return rankingGroupContentRepo.getRankById(id).isPresent() ? rankingGroupContentRepo.getRankById(id).get() : -1;
    }

    public int getRankingTotalProfileViews(long id) {
        return rankingTotalProfileRepo.getRankById(id).isPresent() ? rankingTotalProfileRepo.getRankById(id).get() : -1;
    }

    public int getRankingTotalContentViews(long id)  {
        return rankingTotalContentRepo.getRankById(id).isPresent() ? rankingTotalContentRepo.getRankById(id).get() : -1;
    }


}
