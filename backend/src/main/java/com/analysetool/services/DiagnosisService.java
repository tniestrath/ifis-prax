package com.analysetool.services;

import com.analysetool.modells.*;
import com.analysetool.repositories.*;
import com.analysetool.util.Problem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DiagnosisService {

    final int MAX_CLICKS_IN_CAT_UNTIL_BOT = 5;
    final int MAX_NONSENSE_UNTIL_BOT = 10;

    @Autowired
    universalStatsRepository uniRepo;
    @Autowired
    FinalSearchStatDLCRepository finalSearchStatDLCRepo;
    @Autowired
    PostTypeRepository postTypeRepo;
    @Autowired
    UniversalCategoriesDLCRepository uniCatRepo;
    @Autowired
    UniqueUserService uniqueUserService;
    @Autowired
    TrackingBlacklistRepository tbRepo;
    @Autowired
    PostService postService;
    @Autowired
    WPOptionsRepository wpOpRepo;
    @Autowired
    ExternalServiceRepository externalServiceRepo;
    @Autowired
    LogService logService;

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found. (ordered by descending severity)
     * @throws JSONException .
     */
    public String doCheckUp() throws JSONException {
        JSONArray problems = new JSONArray();

        for(Problem p : allCheckups()) {
            JSONObject json = new JSONObject();
            json.put("severity", p.getSeverity());
            json.put("description", p.getDescription());
            json.put("area", p.getAffectedArea());
            json.put("suggestedSolution", p.getSuggestedSolutions());
            json.put("solutionLink", p.getFullSolutionLink());
            problems.put(json);
        }

        return problems.toString();
    }

    /**
     * An aggregate methods to find Problems in all parts of the database.
     * @return an ordered JSONArray-String, containing information about all Problems that have been found. (ordered by descending severity)
     * @throws JSONException .
     */
    public String doCheckUpHTML() throws JSONException {

        StringBuilder html = new StringBuilder("""
                <!DOCTYPE html><html lang="en"><head>    <meta charset="UTF-8">    <title>Selbstdiagnose</title>    <style>       table {
                    border: 1px solid black;
                    width: 100%;
                    border-collapse: collapse;
                }

                th, td {
                    border-bottom: 2px solid black;
                    padding: 8px;
                    text-align: left;
                }

                th {
                    background-color: #f2f2f2;
                }

                tr:nth-child(even) {
                    background-color: #f2f2f2;
                }

                tr:hover {
                    background-color: #ddd;
                }
                       tr{           border-bottom: 2px solid black;           height: 20px;       }    </style></head><body><table><thead><tr><th>Severity</th><th>Description</th><th>Area</th><th>Suggested Solution</th><th>Solution Link</th></tr></thead><tbody>""");



        for(Problem p : allCheckups()) {
            html.append("<tr>" + "<td>");
            html.append(p.getSeverity());
            html.append("</td>");
            html.append("<td>");
            html.append(p.getDescription());
            html.append("</td>");
            html.append("<td>");
            html.append(p.getAffectedArea());
            html.append("</td>");
            html.append("<td>");
            html.append(p.getSuggestedSolutions());
            html.append("</td>");
            html.append("<td>");
            if(!p.getFullSolutionLink().equals("none")) {
                html.append("<form target='_blank' action=http://").append(p.getFullSolutionLink()).append(" method='post'>").append("<button type=submit>Solve</button>").append("</form>");

            } else {
                html.append("no solution");
            }
            html.append("</td>");
            html.append("</tr>");
        }

        html.append("</tbody>" + "</table>" + "</body>" + "</html>");

        return html.toString();
    }

    /**
     * An aggregate that funnels all Checkups that are currently to be used and returns all of their problems sorted.
     * @return a sorted List of Problems with descending severity.
     */
    private List<Problem> allCheckups() {
        List<Problem> largeList  = new ArrayList<>();

        //Add new lines for new categories of checkups. If any of these are applicable, please add new routines in the respective subroutine.
        largeList.addAll(findUniStatProblems());
        largeList.addAll(findUniDLCProblems());
        largeList.addAll(findGeoProblems());
        largeList.addAll(findSearchStatProblems());
        largeList.addAll(findTypeProblems());
        largeList.addAll(findWebsiteProblems());
        largeList.addAll(findPotentialBots(MAX_CLICKS_IN_CAT_UNTIL_BOT));
        try {
            largeList.addAll(findPotentialBotsByNonsense());
        } catch(Exception e) {
            e.printStackTrace();
        }

        largeList.sort((o1, o2) -> o2.getSeverity() - o1.getSeverity());
        return largeList;
    }

    /**
     * Finds Problems in UniversalStats.
     * @return a List of Problem-Items.
     */
    private List<Problem> findUniStatProblems() {
        List<Problem> list = new ArrayList<>();
        list.addAll(uniDateConsistencyCheckup());
        list.addAll(uniValuesCheckup());
        return list;
    }

    /**
     * Finds Problems in UniversalStats, specifically its values.
     * @return a List of Problem-Items.
     */
    private List<Problem> uniValuesCheckup() {
        List<Problem> list  = new ArrayList<>();
        int severityNegative = 5;
        String descriptionNegative = "A negative Value has been found, where it shouldn't be possible: ";
        String area = "uni-stat-values";

        for(UniversalStats uni : uniRepo.findAllOrderById()) {
            if(uni.getTotalClicks() < 0) {
                list.add(new Problem(severityNegative, descriptionNegative + "totalClicks " + uni.getId(), area));
            } else if(uni.getAnbieter_abolos_anzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterAbolos at " + uni.getId(), area));
            } else if(uni.getAnbieterBasicAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterBasic at " + uni.getId(), area));
            } else if(uni.getAnbieterBasicPlusAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterBasicPlus at " + uni.getId(), area));
            } else if(uni.getAnbieterPlusAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterPlus at " + uni.getId(), area));
            } else if(uni.getAnbieterPremiumAnzahl() <0) {
                list.add(new Problem(severityNegative, descriptionNegative + "anbieterPremium at " + uni.getId(), area));
            }
        }
        return list;
    }

    /**
     * Finds Problems in UniversalStats, specifically its dates.
     * @return a List of Problem-Items.
     */
    private List<Problem> uniDateConsistencyCheckup() {
        List<Problem> list  = new ArrayList<>();

        String area = "unistat-date";
        int severityMissing = 0;
        int severityDuplicate = 5;
        String descriptionMissing = "A missing Date has been found between: ";
        String descriptionDuplicate = "A duplicate Date has been found: ";


        java.sql.Date lastDate = null;
        for (UniversalStats uni : uniRepo.findAllOrderById()) {
            if(uni.getId() > 3450) {
                if (lastDate == null) {
                    lastDate = new java.sql.Date(uni.getDatum().getTime());
                } else {
                    //If two Dates are the same, add a duplicate problem
                    if (lastDate.equals(new java.sql.Date(uni.getDatum().getTime()))) {
                        list.add(new Problem(severityDuplicate, descriptionDuplicate + lastDate, area));
                    } else {
                        //Check whether the distance between the two dates is greater than a day, if so, add a missing Problem.
                        java.sql.Date sqlDate2 = new java.sql.Date(uni.getDatum().getTime());
                        // Calculate difference in milliseconds
                        long differenceInMilliseconds = Math.abs(sqlDate2.getTime() - lastDate.getTime());
                        // Convert milliseconds to days
                        long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);
                        if (differenceInDays > 1) {
                            list.add(new Problem(severityMissing, descriptionMissing + lastDate + " and " + sqlDate2, area));
                        }
                        lastDate = sqlDate2;
                    }
                }
            }
        }
        return list;

    }

    /**
     * Finds Problems in UniversalStats-DLCS.
     * @return a List of Problem-Items.
     */
    private List<Problem> findUniDLCProblems() {
        List<Problem> list = new ArrayList<>();
        list.addAll(uniDLCWrongReferenceCheckup());
        //list.addAll(uniDLCMissingHourCheckup());
        list.addAll(uniDLCValuesCheckup());
        return list;
    }

    /**
     * Finds Problems in UniversalStats-DLCS, specifically references that point to nothing.
     * @return a List of Problem-Items.
     */
    private List<Problem> uniDLCWrongReferenceCheckup() {
        List<Problem> list  = new ArrayList<>();

        String area = "UniDLC";
        int severityError= 5;
        String descriptionUniIdMissing = "A reference to a non-existent UniversalStats has been found. ID noted is: ";
        String descriptionUniIdAfterLatest = "A reference to a UniversalStats of the future has been found. ID noted is: ";

        List<Integer> uniIds = uniRepo.getAllUniIds();

        for(UniversalCategoriesDLC cat : uniCatRepo.findAll()) {
            if(!uniIds.contains(cat.getUniStatId())) {
                if(uniRepo.getLatestUniStat().getId() < cat.getUniStatId()) {
                    list.add(new Problem(severityError, descriptionUniIdAfterLatest + cat.getUniStatId(), area));
                } else {
                    list.add(new Problem(severityError, descriptionUniIdMissing + cat.getUniStatId(), area));
                }
            }
        }
        return list;
    }

    /**
     * Finds Problems in UniversalStats-DLCS, specifically missing Dates.
     * @return a List of Problem-Items.
     */
    private List<Problem> uniDLCMissingHourCheckup() {
        List<Problem> list  = new ArrayList<>();

        String area = "UniDLC";
        int severityError= 2;
        String descriptionHourMissing = "A missing hour has been found, on UniId: ";

        int lastHour = -1;

        for(UniversalCategoriesDLC cat : uniCatRepo.findAll(Sort.by("id"))) {
            if(cat.getUniStatId() > 3450) {
                if (lastHour != -1) {
                    if (lastHour + 1 != cat.getStunde() && lastHour != 23) {
                        list.add(new Problem(severityError, descriptionHourMissing + cat.getUniStatId() + " and between hours: " + lastHour + " " + cat.getStunde(), area));
                    } else if (lastHour == 23 && cat.getStunde() != 0) {
                        list.add(new Problem(severityError, descriptionHourMissing + cat.getUniStatId() + " and between hours: " + lastHour + " " + cat.getStunde(), area));
                    }
                }
                lastHour = cat.getStunde();
            }
        }

        return list;
    }

    /**
     * Finds Problems in UniversalStats-DLCS, specifically bad data.
     * @return a List of Problem-Items.
     */
    private List<Problem> uniDLCValuesCheckup() {
        List<Problem> list  = new ArrayList<>();

        String area = "UniDLC";
        int severityError= 1;
        String descriptionInvaldValue = "An invalid value has been found for UniDLCId: ";


        for(UniversalCategoriesDLC cat : uniCatRepo.findAll()) {
            boolean besucherError= cat.getBesucherGlobal() < 0
                    || cat.getBesucherArticle() < 0
                    || cat.getBesucherNews() < 0
                    || cat.getBesucherBlog() < 0
                    || cat.getBesucherPodcast() < 0
                    || cat.getBesucherWhitepaper() < 0
                    || cat.getBesucherRatgeber() < 0
                    || cat.getBesucherRatgeberPost() < 0
                    || cat.getBesucherRatgeberBuch() < 0
                    || cat.getBesucherRatgeberSelf() < 0
                    || cat.getBesucherMain() < 0
                    || cat.getBesucherUeber() < 0
                    || cat.getBesucherImpressum() < 0
                    || cat.getBesucherPreisliste() < 0
                    || cat.getBesucherPartner() < 0
                    || cat.getBesucherDatenschutz() < 0
                    || cat.getBesucherNewsletter() < 0
                    || cat.getBesucherImage() < 0
                    || cat.getBesucherAGBS() < 0;
            boolean viewError = cat.getViewsGlobal() < 0
                    || cat.getViewsArticle() < 0
                    || cat.getViewsNews() < 0
                    || cat.getViewsBlog() < 0
                    || cat.getViewsPodcast() < 0
                    || cat.getViewsWhitepaper() < 0
                    || cat.getViewsRatgeber() < 0
                    || cat.getViewsRatgeberPost() < 0
                    || cat.getViewsRatgeberBuch() < 0
                    || cat.getViewsRatgeberSelf() < 0
                    || cat.getViewsMain() < 0
                    || cat.getViewsUeber() < 0
                    || cat.getViewsImpressum() < 0
                    || cat.getViewsPreisliste() < 0
                    || cat.getViewsPartner() < 0
                    || cat.getViewsDatenschutz() < 0
                    || cat.getViewsNewsletter() < 0
                    || cat.getViewsImage() < 0
                    || cat.getViewsAGBS() < 0;

            if(besucherError) {
                list.add(new Problem(severityError, descriptionInvaldValue + cat.getId() + " in 'besucher'", area));
            } else if (viewError) {
                list.add(new Problem(severityError, descriptionInvaldValue + cat.getId() + " in 'views'", area));
            }
        }


        return list;
    }

    /**
     * Finds Problems in Geolocation.
     * @return a List of Problem-Items.
     */
    private List<Problem> findGeoProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }

    /**
     * Finds Problems in Search-Stats.
     * @return a List of Problem-Items.
     */
    private List<Problem> findSearchStatProblems() {
        List<Problem> list = new ArrayList<>(successErrorCheck());
        return list;
    }

    /**
     * Checks in FinalSearchStatsDLC, whether there are rows that report a SearchSuccess, without anything being clicked
     * @return a List of Problems.
     */
    private List<Problem> successErrorCheck() {
        List<Problem> list  = new ArrayList<>();

        String area = "SearchSuccess";
        int severityError= 2;
        String descriptionInvalid = "A row with neither a post or user clicked has been found for: ";
        String descriptionNoFinal = "A row with no FinalSearchStatsId has been found for: ";
        String suggestedSol = "Delete entry";
        String solutionLink = "analyse.it-sicherheit.de/api/search-stats/deleteDLCById?id=";

        for(FinalSearchStatDLC f : finalSearchStatDLCRepo.findAll()) {
            if(f.getPostId() == null && f.getUserId() == null) {
                list.add(new Problem(severityError, descriptionInvalid + "SS DLC ID: " + f.getId() + " Final ID: " +f.getFinalSearchId(), area, suggestedSol, solutionLink + f.getId()));
            }
            if(f.getFinalSearchId() == null) {
                list.add(new Problem(severityError, descriptionNoFinal + f.getId() + " Final ID: " +f.getFinalSearchId(), area, suggestedSol, solutionLink + f.getId()));
            }
        }
        return list;
    }

    /**
     * Finds Problems in Post-Types.
     * @return a List of Problem-Items.
     */
    private List<Problem> findTypeProblems() {
        List<Problem> list = new ArrayList<>();
        list.addAll(newTypeFoundCheck());
        list.addAll(changedTypeCheck());
        return list;
    }

    /**
     * Finds Problems in Post-Types, specifically new Types.
     * @return a List of Problem-Items.
     */
    private List<Problem> newTypeFoundCheck() {
        List<Problem> list  = new ArrayList<>();

        String area = "PostTypes";
        int severityError= 3;
        String descriptionNewType = "A new Type has been found: ";
        String solutions = "Add the Type to newTypeFoundCheck or change generating algorithm";

        for(String type : postTypeRepo.getDistinctTypes()) {
            if(!type.equalsIgnoreCase("cyber-risk-check")
                    && !type.equalsIgnoreCase("artikel")
                    && !type.equalsIgnoreCase("ratgeber")
                    && !type.equalsIgnoreCase("news")
                    && !type.equalsIgnoreCase("Event: Sonstige")
                    && !type.equalsIgnoreCase("blog")
                    && !type.equalsIgnoreCase("whitepaper")
                    && !type.equalsIgnoreCase("Event: Kongress")
                    && !type.equalsIgnoreCase("Event: Schulung/Seminar")
                    && !type.equalsIgnoreCase("podcast")
                    && !type.equalsIgnoreCase("Event: Workshop")
                    && !type.equalsIgnoreCase("Event: Messe")
                    && !type.equalsIgnoreCase("videos")) {
                list.add(new Problem(severityError, descriptionNewType + type, area, solutions));
            }
        }

        return list;
    }

    /**
     * Finds Problems in Post-Types, specifically changed Types.
     * @return a List of Problem-Items.
     */
    private List<Problem> changedTypeCheck() {
        List<Problem> list  = new ArrayList<>();

        String area = "PostTypes";
        int severityError= 2;
        String descriptionChangedType = "A Type that has changed has been found. Changed from: ";
        String desPart2 = " to: ";
        String solutions = "Delete the row of Post-Types or change it manually (offered solution is deletion)";
        String solutionLink = "analyse.it-sicherheit.de/api/posts/deletePostTypesById?id=";

        for(PostTypes type : postTypeRepo.findAll()) {
            if(!postService.getType(type.getPost_id()).equals(type.getType())) {
                list.add(new Problem(severityError, (descriptionChangedType + type.getType() + desPart2 + postService.getType(type.getPost_id())), area, solutions, solutionLink + type.getPost_id()));
            }
        }

        return list;
    }

    /**
     * Finds Problems in Website.
     * @return a List of Problem-Items.
     */
    private List<Problem> findWebsiteProblems() {
        List<Problem> list = new ArrayList<>();
        return list;
    }

    /**
     * Finds potential bots by their behavior in clicking nonsense.
     * @return a List of Problem-Items.
     */
    private List<Problem> findPotentialBotsByNonsense() throws JSONException {
        List<Problem> list  = new ArrayList<>();

        String area = "UniqueUserNonsenseDetection";
        int severityNonsense = 4;
        String descriptionPotentialBot = "Potential Bot has been found. Had more nonsense than accepted. IP: ";
        String solutions = "add to Blacklist";
        String solutionLinkBase = "analyse.it-sicherheit.de/api/ip/blockIp?ip=";

        List<String> potentialBots= uniqueUserService.getIpsOfPotBots();
        for(String potBot : potentialBots) {
            if(tbRepo.findByIp(potBot).isEmpty()) {
                list.add(new Problem(severityNonsense, descriptionPotentialBot + potBot, area, solutions, solutionLinkBase + potBot));
            }
        }

        return list;
    }

    /**
     * Finds potential bots by their general behavior.
     * @return a List of Problem-Items.
     */
    private List<Problem> findPotentialBots(int repeatedClicksLimit){
        List<Problem> list  = new ArrayList<>();

        String area = "UniqueUserNormalDetection";
        int severityError= 2;
        int severityNonsense = 4;
        String descriptionPotentialBot = "Potential Bot has been found. IP: ";
        String solutions = "add to Blacklist";
        String solutionLinkBase = "analyse.it-sicherheit.de/api/ip/blockIp?ip=";
        int clicks;
        String ip;

        List<UniqueUser> potentialBots= uniqueUserService.getPossibleBots(repeatedClicksLimit);
        if(!potentialBots.isEmpty()){

            for(UniqueUser potBot: potentialBots){
                if(tbRepo.findByIp(potBot.getIp()).isEmpty()) {
                    try {
                        Map<String, Long> categoryClicksMap = uniqueUserService.getClicksCategory(potBot);
                        if (uniqueUserService.areClicksInSingleCategory(categoryClicksMap)) {
                            String category = uniqueUserService.getCategoryOfClicks(categoryClicksMap);
                            clicks = potBot.getAmount_of_clicks();
                            ip = potBot.getIp();
                            Problem problem;
                            if(category.equals("nonsense")) {
                                problem = new Problem(severityNonsense, descriptionPotentialBot + ip + " ,suspicious click in this category: " + category + ", amount of clicks: " + clicks, area, solutions, solutionLinkBase + potBot.getIp());
                            } else {
                                problem = new Problem(severityError, descriptionPotentialBot + ip + " ,suspicious click in this category: " + category + ", amount of clicks: " + clicks, area, solutions, solutionLinkBase + potBot.getIp());
                            }
                            list.add(problem);
                        }
                    } catch (Exception e) {
                        System.out.println("potential bot processing error :" + Arrays.toString(e.getStackTrace()));
                    }
                }
            }

        }
        return list;
    }

    public String findPotentialBotsTest(int repeatedClicksLimit) {
        return findPotentialBots(repeatedClicksLimit).toString();
    }

    /**
     * Fetches all Data for blocked Bots by the Black Hole Plugin.
     * @return a JSON-Array of JSON-Objects representing their data as a String.
     * @throws JSONException .
     */
    public String getAllBlockedBotsBlackHole() throws JSONException {
        Matcher matcher = Pattern.compile("(?<=\")[^\"]+(?=\";s:\\d+:\"[^\"]+\";)|\\d{4}/\\d{2}/\\d{2} @ \\d{2}:\\d{2}:\\d{2} (?:am|pm)").matcher(wpOpRepo.getAllBlockedBots());
        JSONArray array = new JSONArray();

        String buffer = "lol";
        int i = 1;
        JSONObject json = new JSONObject();
        while(matcher.find()) {
            if(matcher.group(0).equalsIgnoreCase("ip_address")) {
                if(!buffer.equals("lol")) array.put(json);
                json = new JSONObject();
            }
            if(i == 1) {
                buffer = matcher.group(0);
                i++;
            } else if(i == 2) {
                json.put(buffer, matcher.group(0));
                i = 1;
            }
        }
        array.put(json);

        return array.toString();
    }

    /**
     * Fetches all external services that fit inside the given pageable.
     * @param page page number.
     * @param size page size.
     * @return JSON-String containing id, name and link to each external service.
     * @throws JSONException .
     */
    public String getServices(int page, int size) throws JSONException {
        JSONArray array = new JSONArray();

        for(ExternalService e : externalServiceRepo.findAll(PageRequest.of(page, size))) {
            JSONObject obj = new JSONObject();

            obj.put("id", e.getId());
            obj.put("name", e.getName());
            obj.put("link", e.getLink());

            array.put(obj);
        }

        return array.toString();
    }

    public boolean addService(String name, String link) {
        if(externalServiceRepo.findByLinkOrName(link, name).isPresent()) {
            return false;
        } else {
            ExternalService e = new ExternalService();
            e.setLink(link);
            e.setName(name);
            externalServiceRepo.save(e);
            return true;
        }
    }

    public boolean removeService(String name, String link) {
        if(externalServiceRepo.findByLinkOrName(link, name).isPresent()) {
            externalServiceRepo.delete(externalServiceRepo.findByLinkOrName(link, name).get());
            return true;
        } else {
            return false;
        }
    }
}