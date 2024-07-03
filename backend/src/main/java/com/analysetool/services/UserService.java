package com.analysetool.services;

import com.analysetool.api.PostController;
import com.analysetool.modells.*;
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

    private final DashConfig config;

    public UserService(DashConfig config) {
        this.config = config;
    }


    private final String tableBase = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Analyse ihrer Marktplatz Präsens</title></head><body><table id=\"rankings\"><thead><tr><th>Gewählte Themen</th><th>Ihre Platzierung</th><th>Globale Nutzungshäufigkeit</th></tr></thead><tbody>{{TABLEROW}}</tbody></table></body></html>\n";
    private final String tablerowBase = "<tr><td id='rankings-1'>REPLACE1</td><td id='rankings-2'>REPLACE2</td><td id='rankings-3'>REPLACE3</td></tr>";

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
                .replace("{{TENDENCY}}", obj.getBoolean("tendencyUp") ? "HOCH" : "RUNTER")
                .replace("{{REDIRECTS}}", obj.getString("redirects"))
                .replace("{{CONTENTVIEWS}}", obj.getString("postViews"))
                .replace("{{PROFILERANK}}", obj.getString("profileRank"))
                .replace("{{GROUPPROFILERANK}}", obj.getString("profileRankByGroup"))
                .replace("{{CONTENTRANK}}", obj.getString("contentRank"))
                .replace("{{GROUPCONTENTRANK}}", obj.getString("contentRankByGroup"));



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
