package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "wp_em_events")
public class Events implements Serializable {

    @Id
    @Column(name = "event_id")
    private long eventID;

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public long getPostID() {
        return postID;
    }

    public void setPostID(long postID) {
        this.postID = postID;
    }

    public long getEventParent() {
        return eventParent;
    }

    public void setEventParent(long eventParent) {
        this.eventParent = eventParent;
    }

    public String getPostSlug() {
        return postSlug;
    }

    public void setPostSlug(String postSlug) {
        this.postSlug = postSlug;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    public int getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(int eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public Time getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Time eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Time getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Time eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public int getEventAllDay() {
        return eventAllDay;
    }

    public void setEventAllDay(int eventAllDay) {
        this.eventAllDay = eventAllDay;
    }

    public LocalDateTime getEventStart() {
        return eventStart;
    }

    public void setEventStart(LocalDateTime eventStart) {
        this.eventStart = eventStart;
    }

    public LocalDateTime getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(LocalDateTime eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getEventTimezone() {
        return eventTimezone;
    }

    public void setEventTimezone(String eventTimezone) {
        this.eventTimezone = eventTimezone;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public int getEventRSVP() {
        return eventRSVP;
    }

    public void setEventRSVP(int eventRSVP) {
        this.eventRSVP = eventRSVP;
    }

    public Date getRsvpDate() {
        return rsvpDate;
    }

    public void setRsvpDate(Date rsvpDate) {
        this.rsvpDate = rsvpDate;
    }

    public Time getRsvpTime() {
        return rsvpTime;
    }

    public void setRsvpTime(Time rsvpTime) {
        this.rsvpTime = rsvpTime;
    }

    public int getRsvpSpaces() {
        return rsvpSpaces;
    }

    public void setRsvpSpaces(int rsvpSpaces) {
        this.rsvpSpaces = rsvpSpaces;
    }

    public int getEventSpaces() {
        return eventSpaces;
    }

    public void setEventSpaces(int eventSpaces) {
        this.eventSpaces = eventSpaces;
    }

    public int getEventPrivate() {
        return eventPrivate;
    }

    public void setEventPrivate(int eventPrivate) {
        this.eventPrivate = eventPrivate;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public long getRecurrenceID() {
        return recurrenceID;
    }

    public void setRecurrenceID(long recurrenceID) {
        this.recurrenceID = recurrenceID;
    }

    public LocalDateTime getEventDateCreated() {
        return eventDateCreated;
    }

    public void setEventDateCreated(LocalDateTime eventDateCreated) {
        this.eventDateCreated = eventDateCreated;
    }

    public LocalDateTime getEventDateModified() {
        return eventDateModified;
    }

    public void setEventDateModified(LocalDateTime eventDateModified) {
        this.eventDateModified = eventDateModified;
    }

    public int getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(int recurrence) {
        this.recurrence = recurrence;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public String getRecurrenceFreq() {
        return recurrenceFreq;
    }

    public void setRecurrenceFreq(String recurrenceFreq) {
        this.recurrenceFreq = recurrenceFreq;
    }

    public String getRecurrenceByDay() {
        return recurrenceByDay;
    }

    public void setRecurrenceByDay(String recurrenceByDay) {
        this.recurrenceByDay = recurrenceByDay;
    }

    public int getRecurrenceByWeekNo() {
        return recurrenceByWeekNo;
    }

    public void setRecurrenceByWeekNo(int recurrenceByWeekNo) {
        this.recurrenceByWeekNo = recurrenceByWeekNo;
    }

    public int getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(int recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

    public int getRecurrenceRSVPDays() {
        return recurrenceRSVPDays;
    }

    public void setRecurrenceRSVPDays(int recurrenceRSVPDays) {
        this.recurrenceRSVPDays = recurrenceRSVPDays;
    }

    public long getBlogID() {
        return blogID;
    }

    public void setBlogID(long blogID) {
        this.blogID = blogID;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public String getEventLanguage() {
        return eventLanguage;
    }

    public void setEventLanguage(String eventLanguage) {
        this.eventLanguage = eventLanguage;
    }

    public int getEventTranslation() {
        return eventTranslation;
    }

    public void setEventTranslation(int eventTranslation) {
        this.eventTranslation = eventTranslation;
    }

    public int getEventActive() {
        return eventActive;
    }

    public void setEventActive(int eventActive) {
        this.eventActive = eventActive;
    }

    @Column(name = "post_id")
    private long postID;

    @Column(name = "event_parent")
    private Long eventParent;

    @Column(name = "event_slug")
    private String postSlug;

    @Column(name="event_owner")
    private long ownerID;

    @Column(name="event_status")
    private int eventStatus;

    @Column(name="event_name")
    private String eventName;

    @Column(name="event_start_date")
    private Date eventStartDate;

    @Column(name="event_end_date")
    private Date eventEndDate;

    @Column(name="event_start_time")
    private Time eventStartTime;

    @Column(name="event_end_time")
    private Time eventEndTime;

    @Column(name="event_all_day")
    private int eventAllDay;

    @Column(name="event_start")
    private LocalDateTime eventStart;

    @Column(name="event_end")
    private LocalDateTime eventEnd;

    @Column(name="event_timezone")
    private String eventTimezone;

    @Column(name="post_content")
    private String postContent;

    @Column(name="event_rsvp")
    private int eventRSVP;

    @Column(name="event_rsvp_date")
    private Date rsvpDate;

    @Column(name="event_rsvp_time")
    private Time rsvpTime;

    @Column(name="event_rsvp_spaces")
    private int rsvpSpaces;

    @Column(name="event_spaces")
    private int eventSpaces;

    @Column(name="event_private")
    private int eventPrivate;

    @Column(name="location_id")
    private long locationID;

    @Column(name="event_location_type")
    private String locationType;

    @Column(name="recurrence_id")
    private long recurrenceID;

    @Column(name="event_date_created")
    private LocalDateTime eventDateCreated;

    @Column(name="event_date_modified")
    private LocalDateTime eventDateModified;

    @Column(name="recurrence")
    private int recurrence;

    @Column(name="recurrence_interval")
    private int recurrenceInterval;

    @Column(name="recurrence_freq")
    private String recurrenceFreq;

    @Column(name="recurrence_byday")
    private String recurrenceByDay;

    @Column(name="recurrence_byweekno")
    private int recurrenceByWeekNo;

    @Column(name="recurrence_days")
    private int recurrenceDays;

    @Column(name="recurrence_rsvp_days")
    private int recurrenceRSVPDays;

    @Column(name="blog_id")
    private long blogID;

    @Column(name="group_id")
    private long groupID;

    @Column(name="event_language")
    private String eventLanguage;

    @Column(name="event_translation")
    private int eventTranslation;

    @Column(name="event_active_status")
    private int eventActive;


}
