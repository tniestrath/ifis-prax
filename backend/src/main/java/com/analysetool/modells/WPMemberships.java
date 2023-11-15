package com.analysetool.modells;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "wp_pmpro_memberships_users")
public class WPMemberships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="user_id")
    private long user_id;

    @Column(name="membership_id")
    private long membership_id;

    @Column(name="code_id")
    private long code_id;

    @Column(name="initial_payment")
    private double initial_payment;

    @Column(name="billing_amount")
    private double billing_amount;

    @Column(name="cycle_number")
    private long cycle_number;

    @Column(name="cycle_period")
    private String cycle_period;

    @Column(name="billing_limit")
    private long billing_limit;

    @Column(name="trial_amount")
    private long trial_amount;

    @Column(name="trial_limit")
    private int trial_limit;

    @Column(name="status")
    private String status;

    @Column(name="startdate")
    private LocalDateTime startdate;

    @Column(name="enddate")
    private LocalDateTime enddate;

    @Column(name="modified")
    private Timestamp modified;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getMembership_id() {
        return membership_id;
    }

    public void setMembership_id(long membership_id) {
        this.membership_id = membership_id;
    }

    public long getCode_id() {
        return code_id;
    }

    public void setCode_id(long code_id) {
        this.code_id = code_id;
    }

    public double getInitial_payment() {
        return initial_payment;
    }

    public void setInitial_payment(double initial_payment) {
        this.initial_payment = initial_payment;
    }

    public double getBilling_amount() {
        return billing_amount;
    }

    public void setBilling_amount(double billing_amount) {
        this.billing_amount = billing_amount;
    }

    public long getCycle_number() {
        return cycle_number;
    }

    public void setCycle_number(long cycle_number) {
        this.cycle_number = cycle_number;
    }

    public String getCycle_period() {
        return cycle_period;
    }

    public void setCycle_period(String cycle_period) {
        this.cycle_period = cycle_period;
    }

    public long getBilling_limit() {
        return billing_limit;
    }

    public void setBilling_limit(long billing_limit) {
        this.billing_limit = billing_limit;
    }

    public long getTrial_amount() {
        return trial_amount;
    }

    public void setTrial_amount(long trial_amount) {
        this.trial_amount = trial_amount;
    }

    public int getTrial_limit() {
        return trial_limit;
    }

    public void setTrial_limit(int trial_limit) {
        this.trial_limit = trial_limit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDateTime startdate) {
        this.startdate = startdate;
    }

    public LocalDateTime getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDateTime enddate) {
        this.enddate = enddate;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
}
