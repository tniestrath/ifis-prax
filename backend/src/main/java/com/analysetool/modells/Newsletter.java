package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "wp_newsletter")
public class Newsletter implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;
    @Column(name = "token")
    private String token;

    @Column(name = "language")
    private String language;

    @Column(name = "status")
    private char status;

    @Column(name = "profile")
    private String profile;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private int updated;

    @Column(name = "last_activity")
    private int last_activity;

    @Column(name = "followup_step")
    private int followup_step;

    @Column(name = "followup_time")
    private Long followup_time;

    @Column(name = "followup")
    private int followup;

    @Column(name = "surname")
    private String surname;

    @Column(name = "sex")
    private char sex;

    @Column(name = "feed_time")
    private Long feed_time;

    @Column(name = "feed")
    private int feed;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "ip")
    private String ip;

    @Column(name = "wp_user_id")
    private int wp_user_id;

    @Column(name = "http_referer")
    private String http_referer;

    @Column(name = "geo")
    private int geo;

    @Column(name = "country")
    private String country;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "bounce_type")
    private String bounce_type;

    @Column(name = "bounce_time")
    private int bounce_time;

    @Column(name = "unsub_email_id")
    private int unsub_email_id;

    @Column(name = "unsub_time")
    private int unsub_time;

    @Column(name = "list_1")
    private int list_1;

    @Column(name = "list_2")
    private int list_2;

    @Column(name = "list_3")
    private int list_3;

    @Column(name = "list_4")
    private int list_4;

    @Column(name = "list_5")
    private int list_5;

    @Column(name = "list_6")
    private int list_6;

    @Column(name = "list_7")
    private int list_7;

    @Column(name = "list_8")
    private int list_8;

    @Column(name = "list_9")
    private int list_9;

    @Column(name = "list_10")
    private int list_10;

    @Column(name = "list_11")
    private int list_11;

    @Column(name = "list_12")
    private int list_12;

    @Column(name = "list_13")
    private int list_13;

    @Column(name = "list_14")
    private int list_14;

    @Column(name = "list_15")
    private int list_15;

    @Column(name = "list_16")
    private int list_16;

    @Column(name = "list_17")
    private int list_17;

    @Column(name = "list_18")
    private int list_18;

    @Column(name = "list_19")
    private int list_19;

    @Column(name = "list_20")
    private int list_20;

    @Column(name = "list_21")
    private int list_21;

    @Column(name = "list_22")
    private int list_22;

    @Column(name = "list_23")
    private int list_23;

    @Column(name = "list_24")
    private int list_24;

    @Column(name = "list_25")
    private int list_25;

    @Column(name = "list_26")
    private int list_26;

    @Column(name = "list_27")
    private int list_27;

    @Column(name = "list_28")
    private int list_28;

    @Column(name = "list_29")
    private int list_29;

    @Column(name = "list_30")
    private int list_30;

    @Column(name = "list_31")
    private int list_31;

    @Column(name = "list_32")
    private int list_32;

    @Column(name = "list_33")
    private int list_33;

    @Column(name = "list_34")
    private int list_34;

    @Column(name = "list_35")
    private int list_35;

    @Column(name = "list_36")
    private int list_36;

    @Column(name = "list_37")
    private int list_37;

    @Column(name = "list_38")
    private int list_38;

    @Column(name = "list_39")
    private int list_39;

    @Column(name = "list_40")
    private int list_40;

    @Column(name = "profile_1")
    private String profile_1;

    @Column(name = "profile_2")
    private String profile_2;

    @Column(name = "profile_3")
    private String profile_3;

    @Column(name = "profile_4")
    private String profile_4;

    @Column(name = "profile_5")
    private String profile_5;

    @Column(name = "profile_6")
    private String profile_6;

    @Column(name = "profile_7")
    private String profile_7;

    @Column(name = "profile_8")
    private String profile_8;

    @Column(name = "profile_9")
    private String profile_9;

    @Column(name = "profile_10")
    private String profile_10;

    @Column(name = "profile_11")
    private String profile_11;

    @Column(name = "profile_12")
    private String profile_12;

    @Column(name = "profile_13")
    private String profile_13;

    @Column(name = "profile_14")
    private String profile_14;

    @Column(name = "profile_15")
    private String profile_15;

    @Column(name = "profile_16")
    private String profile_16;

    @Column(name = "profile_17")
    private String profile_17;

    @Column(name = "profile_18")
    private String profile_18;

    @Column(name = "profile_19")
    private String profile_19;

    @Column(name = "profile_20")
    private String profile_20;

    @Column(name = "test")
    private int test;

    @Column(name = "source")
    private String source;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getLast_activity() {
        return last_activity;
    }

    public void setLast_activity(int last_activity) {
        this.last_activity = last_activity;
    }

    public int getFollowup_step() {
        return followup_step;
    }

    public void setFollowup_step(int followup_step) {
        this.followup_step = followup_step;
    }

    public Long getFollowup_time() {
        return followup_time;
    }

    public void setFollowup_time(Long followup_time) {
        this.followup_time = followup_time;
    }

    public int getFollowup() {
        return followup;
    }

    public void setFollowup(int followup) {
        this.followup = followup;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public Long getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(Long feed_time) {
        this.feed_time = feed_time;
    }

    public int getFeed() {
        return feed;
    }

    public void setFeed(int feed) {
        this.feed = feed;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getWp_user_id() {
        return wp_user_id;
    }

    public void setWp_user_id(int wp_user_id) {
        this.wp_user_id = wp_user_id;
    }

    public String getHttp_referer() {
        return http_referer;
    }

    public void setHttp_referer(String http_referer) {
        this.http_referer = http_referer;
    }

    public int getGeo() {
        return geo;
    }

    public int getList_16() {
        return list_16;
    }

    public void setList_16(int list_16) {
        this.list_16 = list_16;
    }

    public int getList_17() {
        return list_17;
    }

    public void setList_17(int list_17) {
        this.list_17 = list_17;
    }

    public int getList_18() {
        return list_18;
    }

    public void setList_18(int list_18) {
        this.list_18 = list_18;
    }

    public int getList_19() {
        return list_19;
    }

    public void setList_19(int list_19) {
        this.list_19 = list_19;
    }

    public int getList_20() {
        return list_20;
    }

    public void setList_20(int list_20) {
        this.list_20 = list_20;
    }

    public int getList_21() {
        return list_21;
    }

    public void setList_21(int list_21) {
        this.list_21 = list_21;
    }

    public int getList_22() {
        return list_22;
    }

    public void setList_22(int list_22) {
        this.list_22 = list_22;
    }

    public int getList_23() {
        return list_23;
    }

    public void setList_23(int list_23) {
        this.list_23 = list_23;
    }

    public int getList_24() {
        return list_24;
    }

    public void setList_24(int list_24) {
        this.list_24 = list_24;
    }

    public int getList_25() {
        return list_25;
    }

    public void setList_25(int list_25) {
        this.list_25 = list_25;
    }

    public int getList_26() {
        return list_26;
    }

    public void setList_26(int list_26) {
        this.list_26 = list_26;
    }

    public int getList_27() {
        return list_27;
    }

    public void setList_27(int list_27) {
        this.list_27 = list_27;
    }

    public int getList_28() {
        return list_28;
    }

    public void setList_28(int list_28) {
        this.list_28 = list_28;
    }

    public int getList_29() {
        return list_29;
    }

    public void setList_29(int list_29) {
        this.list_29 = list_29;
    }

    public int getList_30() {
        return list_30;
    }

    public void setList_30(int list_30) {
        this.list_30 = list_30;
    }

    public int getList_31() {
        return list_31;
    }

    public void setList_31(int list_31) {
        this.list_31 = list_31;
    }

    public int getList_32() {
        return list_32;
    }

    public void setList_32(int list_32) {
        this.list_32 = list_32;
    }

    public int getList_33() {
        return list_33;
    }

    public void setList_33(int list_33) {
        this.list_33 = list_33;
    }

    public int getList_34() {
        return list_34;
    }

    public void setList_34(int list_34) {
        this.list_34 = list_34;
    }

    public int getList_35() {
        return list_35;
    }

    public void setList_35(int list_35) {
        this.list_35 = list_35;
    }

    public int getList_36() {
        return list_36;
    }

    public void setList_36(int list_36) {
        this.list_36 = list_36;
    }

    public int getList_37() {
        return list_37;
    }

    public void setList_37(int list_37) {
        this.list_37 = list_37;
    }

    public int getList_38() {
        return list_38;
    }

    public void setList_38(int list_38) {
        this.list_38 = list_38;
    }

    public int getList_39() {
        return list_39;
    }

    public void setList_39(int list_39) {
        this.list_39 = list_39;
    }

    public int getList_40() {
        return list_40;
    }

    public void setList_40(int list_40) {
        this.list_40 = list_40;
    }

    public String getProfile_1() {
        return profile_1;
    }

    public void setProfile_1(String profile_1) {
        this.profile_1 = profile_1;
    }

    public String getProfile_2() {
        return profile_2;
    }

    public void setProfile_2(String profile_2) {
        this.profile_2 = profile_2;
    }

    public String getProfile_3() {
        return profile_3;
    }

    public void setProfile_3(String profile_3) {
        this.profile_3 = profile_3;
    }

    public String getProfile_4() {
        return profile_4;
    }

    public void setProfile_4(String profile_4) {
        this.profile_4 = profile_4;
    }

    public String getProfile_5() {
        return profile_5;
    }

    public void setProfile_5(String profile_5) {
        this.profile_5 = profile_5;
    }

    public String getProfile_6() {
        return profile_6;
    }

    public void setProfile_6(String profile_6) {
        this.profile_6 = profile_6;
    }

    public String getProfile_7() {
        return profile_7;
    }

    public void setProfile_7(String profile_7) {
        this.profile_7 = profile_7;
    }

    public String getProfile_8() {
        return profile_8;
    }

    public void setProfile_8(String profile_8) {
        this.profile_8 = profile_8;
    }

    public String getProfile_9() {
        return profile_9;
    }

    public void setProfile_9(String profile_9) {
        this.profile_9 = profile_9;
    }

    public String getProfile_10() {
        return profile_10;
    }

    public void setProfile_10(String profile_10) {
        this.profile_10 = profile_10;
    }

    public String getProfile_11() {
        return profile_11;
    }

    public void setProfile_11(String profile_11) {
        this.profile_11 = profile_11;
    }

    public String getProfile_12() {
        return profile_12;
    }

    public void setProfile_12(String profile_12) {
        this.profile_12 = profile_12;
    }

    public String getProfile_13() {
        return profile_13;
    }

    public void setProfile_13(String profile_13) {
        this.profile_13 = profile_13;
    }

    public String getProfile_14() {
        return profile_14;
    }

    public void setProfile_14(String profile_14) {
        this.profile_14 = profile_14;
    }

    public String getProfile_15() {
        return profile_15;
    }

    public void setProfile_15(String profile_15) {
        this.profile_15 = profile_15;
    }

    public String getProfile_16() {
        return profile_16;
    }

    public void setProfile_16(String profile_16) {
        this.profile_16 = profile_16;
    }

    public String getProfile_17() {
        return profile_17;
    }

    public void setProfile_17(String profile_17) {
        this.profile_17 = profile_17;
    }

    public String getProfile_18() {
        return profile_18;
    }

    public void setProfile_18(String profile_18) {
        this.profile_18 = profile_18;
    }

    public String getProfile_19() {
        return profile_19;
    }

    public void setProfile_19(String profile_19) {
        this.profile_19 = profile_19;
    }

    public String getProfile_20() {
        return profile_20;
    }

    public void setProfile_20(String profile_20) {
        this.profile_20 = profile_20;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setGeo(int geo) {
        this.geo = geo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBounce_type() {
        return bounce_type;
    }

    public void setBounce_type(String bounce_type) {
        this.bounce_type = bounce_type;
    }

    public int getBounce_time() {
        return bounce_time;
    }

    public void setBounce_time(int bounce_time) {
        this.bounce_time = bounce_time;
    }

    public int getUnsub_email_id() {
        return unsub_email_id;
    }

    public void setUnsub_email_id(int unsub_email_id) {
        this.unsub_email_id = unsub_email_id;
    }

    public int getUnsub_time() {
        return unsub_time;
    }

    public void setUnsub_time(int unsub_time) {
        this.unsub_time = unsub_time;
    }

    public int getList_1() {
        return list_1;
    }

    public void setList_1(int list_1) {
        this.list_1 = list_1;
    }

    public int getList_2() {
        return list_2;
    }

    public void setList_2(int list_2) {
        this.list_2 = list_2;
    }

    public int getList_3() {
        return list_3;
    }

    public void setList_3(int list_3) {
        this.list_3 = list_3;
    }

    public int getList_4() {
        return list_4;
    }

    public void setList_4(int list_4) {
        this.list_4 = list_4;
    }

    public int getList_5() {
        return list_5;
    }

    public void setList_5(int list_5) {
        this.list_5 = list_5;
    }

    public int getList_6() {
        return list_6;
    }

    public void setList_6(int list_6) {
        this.list_6 = list_6;
    }

    public int getList_7() {
        return list_7;
    }

    public void setList_7(int list_7) {
        this.list_7 = list_7;
    }

    public int getList_8() {
        return list_8;
    }

    public void setList_8(int list_8) {
        this.list_8 = list_8;
    }

    public int getList_9() {
        return list_9;
    }

    public void setList_9(int list_9) {
        this.list_9 = list_9;
    }

    public int getList_10() {
        return list_10;
    }

    public void setList_10(int list_10) {
        this.list_10 = list_10;
    }

    public int getList_11() {
        return list_11;
    }

    public void setList_11(int list_11) {
        this.list_11 = list_11;
    }

    public int getList_12() {
        return list_12;
    }

    public void setList_12(int list_12) {
        this.list_12 = list_12;
    }

    public int getList_13() {
        return list_13;
    }

    public void setList_13(int list_13) {
        this.list_13 = list_13;
    }

    public int getList_14() {
        return list_14;
    }

    public void setList_14(int list_14) {
        this.list_14 = list_14;
    }

    public int getList_15() {
        return list_15;
    }

    public void setList_15(int list_15) {
        this.list_15 = list_15;
    }

}
