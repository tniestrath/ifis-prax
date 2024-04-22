package com.analysetool.modells;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Entity
@Table(name = "stats")

public class PostStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "art_id")
    private Long artId;

    @Column(name = "search_success_rate")
    private Float searchSuccessRate;

    @Column(name = "article_referring_rate")
    private Float articleReferringRate;

    @Column(name = "clicks")
    private Long clicks;
    // Constructor, getters, and setters
    @Column(name="search_succes")
    private long searchSuccess;

    @Column(name="refferings")
    private long refferings;

    @Column(name="performance")
    private float performance;

    @Column(name="relevance")
    private float relevance;
    @Column(name="year")
    private int year;

    @Lob
    @Column(name = "views_last_year", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Long> viewsLastYear = new HashMap<>();
    @Lob
    @Column(name = "views_per_hour", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Long> viewsPerHour = new HashMap<>();


    @Column(name="lettercount", columnDefinition = "int")
    private int lettercount;

    @Column(name="wordcount", columnDefinition = "int")
    private int wordcount;
    @Transient
    String viewsPerDay = "{\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0,\"13\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"20\":0,\"21\":0,\"22\":0,\"23\":0,\"24\":0,\"25\":0,\"26\":0,\"27\":0,\"28\":0,\"29\":0,\"30\":0,\"31\":0,\"32\":0,\"33\":0,\"34\":0,\"35\":0,\"36\":0,\"37\":0,\"38\":0,\"39\":0,\"40\":0,\"41\":0,\"42\":0,\"43\":0,\"44\":0,\"45\":0,\"46\":0,\"47\":0,\"48\":0,\"49\":0,\"50\":0,\"51\":0,\"52\":0,\"53\":0,\"54\":0,\"55\":0,\"56\":0,\"57\":0,\"58\":0,\"59\":0,\"60\":0,\"61\":0,\"62\":0,\"63\":0,\"64\":0,\"65\":0,\"66\":0,\"67\":0,\"68\":0,\"69\":0,\"70\":0,\"71\":0,\"72\":0,\"73\":0,\"74\":0,\"75\":0,\"76\":0,\"77\":0,\"78\":0,\"79\":0,\"80\":0,\"81\":0,\"82\":0,\"83\":0,\"84\":0,\"85\":0,\"86\":0,\"87\":0,\"88\":0,\"89\":0,\"90\":0,\"91\":0,\"92\":0,\"93\":0,\"94\":0,\"95\":0,\"96\":0,\"97\":0,\"98\":0,\"99\":0,\"100\":0,\"101\":0,\"102\":0,\"103\":0,\"104\":0,\"105\":0,\"106\":0,\"107\":0,\"108\":0,\"109\":0,\"110\":0,\"111\":0,\"112\":0,\"113\":0,\"114\":0,\"115\":0,\"116\":0,\"117\":0,\"118\":0,\"119\":0,\"120\":0,\"121\":0,\"122\":0,\"123\":0,\"124\":0,\"125\":0,\"126\":0,\"127\":0,\"128\":0,\"129\":0,\"130\":0,\"131\":0,\"132\":0,\"133\":0,\"134\":0,\"135\":0,\"136\":0,\"137\":0,\"138\":0,\"139\":0,\"140\":0,\"141\":0,\"142\":0,\"143\":0,\"144\":0,\"145\":0,\"146\":0,\"147\":0,\"148\":0,\"149\":0,\"150\":0,\"151\":0,\"152\":0,\"153\":0,\"154\":0,\"155\":0,\"156\":0,\"157\":0,\"158\":0,\"159\":0,\"160\":0,\"161\":0,\"162\":0,\"163\":0,\"164\":0,\"165\":0,\"166\":0,\"167\":0,\"168\":0,\"169\":0,\"170\":0,\"171\":0,\"172\":0,\"173\":0,\"174\":0,\"175\":0,\"176\":0,\"177\":0,\"178\":0,\"179\":0,\"180\":0,\"181\":0,\"182\":0,\"183\":0,\"184\":0,\"185\":0,\"186\":0,\"187\":0,\"188\":0,\"189\":0,\"190\":0,\"191\":0,\"192\":0,\"193\":0,\"194\":0,\"195\":0,\"196\":0,\"197\":0,\"198\":0,\"199\":0,\"200\":0,\"201\":0,\"202\":0,\"203\":0,\"204\":0,\"205\":0,\"206\":0,\"207\":0,\"208\":0,\"209\":0,\"210\":0,\"211\":0,\"212\":0,\"213\":0,\"214\":0,\"215\":0,\"216\":0,\"217\":0,\"218\":0,\"219\":0,\"220\":0,\"221\":0,\"222\":0,\"223\":0,\"224\":0,\"225\":0,\"226\":0,\"227\":0,\"228\":0,\"229\":0,\"230\":0,\"231\":0,\"232\":0,\"233\":0,\"234\":0,\"235\":0,\"236\":0,\"237\":0,\"238\":0,\"239\":0,\"240\":0,\"241\":0,\"242\":0,\"243\":0,\"244\":0,\"245\":0,\"246\":0,\"247\":0,\"248\":0,\"249\":0,\"250\":0,\"251\":0,\"252\":0,\"253\":0,\"254\":0,\"255\":0,\"256\":0,\"257\":0,\"258\":0,\"259\":0,\"260\":0,\"261\":0,\"262\":0,\"263\":0,\"264\":0,\"265\":0,\"266\":0,\"267\":0,\"268\":0,\"269\":0,\"270\":0,\"271\":0,\"272\":0,\"273\":0,\"274\":0,\"275\":0,\"276\":0,\"277\":0,\"278\":0,\"279\":0,\"280\":0,\"281\":0,\"282\":0,\"283\":0,\"284\":0,\"285\":0,\"286\":0,\"287\":0,\"288\":0,\"289\":0,\"290\":0,\"291\":0,\"292\":0,\"293\":0,\"294\":0,\"295\":0,\"296\":0,\"297\":0,\"298\":0,\"299\":0,\"300\":0,\"301\":0,\"302\":0,\"303\":0,\"304\":0,\"305\":0,\"306\":0,\"307\":0,\"308\":0,\"309\":0,\"310\":0,\"311\":0,\"312\":0,\"313\":0,\"314\":0,\"315\":0,\"316\":0,\"317\":0,\"318\":0,\"319\":0,\"320\":0,\"321\":0,\"322\":0,\"323\":0,\"324\":0,\"325\":0,\"326\":0,\"327\":0,\"328\":0,\"329\":0,\"330\":0,\"331\":0,\"332\":0,\"333\":0,\"334\":0,\"335\":0,\"336\":0,\"337\":0,\"338\":0,\"339\":0,\"340\":0,\"341\":0,\"342\":0,\"343\":0,\"344\":0,\"345\":0,\"346\":0,\"347\":0,\"348\":0,\"349\":0,\"350\":0,\"351\":0,\"352\":0,\"353\":0,\"354\":0,\"355\":0,\"356\":0,\"357\":0,\"358\":0,\"359\":0,\"360\":0,\"361\":0,\"362\":0,\"363\":0,\"364\":0,\"365\":0}";

    @Transient
    String viewsPerHourString = "{\"0\":0,\"1\":0,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0,\"13\":0,\"14\":0,\"15\":0,\"16\":0,\"17\":0,\"18\":0,\"19\":0,\"20\":0,\"21\":0,\"22\":0,\"23\":0}";
    public PostStats(){}

    public long getSearchSucces() {
        return searchSuccess;
    }

    public void setSearchSucces(long searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public long getReferrings() {
        return refferings;
    }
    public void setReferrings(long referrings) {
        this.refferings = referrings;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }

    public int getLettercount() { return lettercount; }

    public PostStats(Long artId, Float searchSuccessRate, Float articleReferringRate, long clicks, long searchSuccess, long refferings, float performance) {
        this.artId = artId;
        this.searchSuccessRate = searchSuccessRate;
        this.articleReferringRate = articleReferringRate;
        this.clicks = clicks;
        this.refferings=refferings;
        this.searchSuccess=searchSuccess;
        this.performance = performance;
        this.relevance=(float)0;
        //this.viewsLastYear=setJson();
        this.viewsLastYear=setViewsLastYearJson();
        this.viewsPerHour=setJson();
        Calendar kalender = Calendar.getInstance();
        this.year= kalender.get(Calendar.YEAR);
    }

    public Map<String,Long> setViewsLastYearJson(){
        // Erstellen einer HashMap mit dem Schlüssel als Datum (z.B. 01.01) und dem Wert 0
        Calendar kalender = Calendar.getInstance();
        int aktuellesJahr = kalender.get(Calendar.YEAR);
        Map<String, Long> tageDesJahres = new HashMap<>();

        // Format für das Datum festlegen
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        // Startdatum auf den ersten Januar setzen
        LocalDate datum = LocalDate.of(aktuellesJahr, 1, 1);

        // Durch alle Tage des Jahres iterieren
        while (datum.getYear() == 2023) {
            // Datum formatieren und in die HashMap mit dem Wert 0 einfügen
            tageDesJahres.put(datum.format(formatter), (long)0);
            // Datum um einen Tag erhöhen
            datum = datum.plusDays(1);
        }
        return tageDesJahres;
    }
    public Map<String, Long> createYearlyViewsMap(int year) {
        // Erstellen einer HashMap mit dem Schlüssel als Datum (z.B. 01.01) und dem Wert 0
        Map<String, Long> daysOfYear = new HashMap<>();

        // Format für das Datum festlegen
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        // Startdatum auf den ersten Januar setzen
        LocalDate date = LocalDate.of(year, 1, 1);

        // Durch alle Tage des Jahres iterieren
        while (date.getYear() == year) {
            // Datum formatieren und in die HashMap mit dem Wert 0 einfügen
            daysOfYear.put(date.format(formatter), 0L);
            // Datum um einen Tag erhöhen
            date = date.plusDays(1);
        }

        return daysOfYear;
    }

    public Map<String,Long> setJson(){
        String temp;
        //temp = viewsPerDay.substring(1, viewsPerDay.length() - 1);
        temp = viewsPerHourString.substring(1, viewsPerHourString.length() - 1);
        // Teile den String an den Kommas auf, um die einzelnen Schlüssel-Wert-Paare zu erhalten
        String[] keyValuePairs = temp.split(",");

        // Erstelle eine HashMap, um die Schlüssel-Wert-Paare zu speichern
        HashMap<String, Long> map = new HashMap<>();

        // Iteriere über die einzelnen Schlüssel-Wert-Paare und füge sie der HashMap hinzu
        for (String pair : keyValuePairs) {
            // Teile den Schlüssel-Wert-Paar-String an den Doppelpunkten auf
            String[] entry = pair.split(":");

            // Entferne führende und abschließende Anführungszeichen von Schlüssel und Wert
            String key = entry[0].trim().replaceAll("\"", "");
           long value = Long.parseLong(entry[1].trim());

            // Füge das Schlüssel-Wert-Paar der HashMap hinzu
            map.put(key, value);
        }
        return map;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtId() {
        return artId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostStats PostStats)) return false;
        return getSearchSuccess() == PostStats.getSearchSuccess() && getReferrings() == PostStats.getReferrings() && Float.compare(PostStats.getPerformance(), getPerformance()) == 0 && Objects.equals(getId(), PostStats.getId()) && Objects.equals(getArtId(), PostStats.getArtId()) && Objects.equals(getSearchSuccessRate(), PostStats.getSearchSuccessRate()) && Objects.equals(getArticleReferringRate(), PostStats.getArticleReferringRate()) && Objects.equals(getClicks(), PostStats.getClicks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getArtId(), getSearchSuccessRate(), getArticleReferringRate(), getClicks(), getSearchSuccess(), getReferrings(), getPerformance());
    }

    public long getSearchSuccess() {
        return searchSuccess;
    }

    public void setSearchSuccess(long searchSuccess) {
        this.searchSuccess = searchSuccess;
    }

    public float getPerformance() {
        return performance;
    }

    public void setPerformance(float performance) {
        this.performance = performance;
    }

    public void setArtId(Long artId) {
        this.artId = artId;
    }

    public Float getSearchSuccessRate() {
        return searchSuccessRate;
    }

    public void setSearchSuccessRate(Float searchSuccessRate) {
        this.searchSuccessRate = searchSuccessRate;
    }

    public Float getArticleReferringRate() {
        return articleReferringRate;
    }

    public long getRefferings() {
        return refferings;
    }

    public void setRefferings(long refferings) {
        this.refferings = refferings;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Map<String, Long> getViewsPerHour() {
        return viewsPerHour;
    }

    public void setViewsPerHour(Map<String, Long> viewsPerHour) {
        this.viewsPerHour = viewsPerHour;
    }

    public String getViewsPerDay() {
        return viewsPerDay;
    }

    public void setViewsPerDay(String viewsPerDay) {
        this.viewsPerDay = viewsPerDay;
    }

    public String getViewsPerHourString() {
        return viewsPerHourString;
    }

    public void setViewsPerHourString(String viewsPerHourString) {
        this.viewsPerHourString = viewsPerHourString;
    }

    public Map<String, Long> getViewsLastYear() {
        return viewsLastYear;
    }

    public void setViewsLastYear(Map<String, Long> viewsLastYear) {
        this.viewsLastYear = viewsLastYear;
    }

    public void setArticleReferringRate(Float articleReferringRate) {
        this.articleReferringRate = articleReferringRate;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    @Override
    public String toString() {
        return "PostStats{" +
                "id=" + id +
                ", artId=" + artId +
                ", searchSuccessRate=" + searchSuccessRate +
                ", articleReferringRate=" + articleReferringRate +
                ", clicks=" + clicks +
                ", searchSuccess=" + searchSuccess +
                ", referrings=" + refferings +
                ", performance=" + performance +
                '}';
    }

    public void setLettercount(int lettercount) {
        this.lettercount = lettercount;
    }

    public int getWordcount() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }
}