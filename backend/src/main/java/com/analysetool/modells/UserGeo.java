package com.analysetool.modells;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "user_geo")
public class UserGeo implements Serializable {

    @Id
    private Long user_id;

    @Column(name="uni_stat_id")
    private int uniStatId;

    @Column(name="HH")
    private int hh;

    @Column(name="HB")
    private int hb;

    @Column(name="BE")
    private int be;

    @Column(name="MV")
    private int mv;

    @Column(name="BB")
    private int bb;

    @Column(name="SN")
    private int sn;

    @Column(name="ST")
    private int st;

    @Column(name="BYE")
    private int bye;

    @Column(name="SL")
    private int sl;

    @Column(name="RP")
    private int rp;

    @Column(name="SH")
    private int sh;

    @Column(name="TH")
    private int th;

    @Column(name="NB")
    private int nb;

    @Column(name="HE")
    private int he;

    @Column(name="BW")
    private int BW;

    @Column(name="NW")
    private int NW;

    @Column(name="Ausland")
    private int ausland;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long post_id) {
        this.user_id = post_id;
    }

    public int getUniStatId() {
        return uniStatId;
    }

    public void setUniStatId(int uniStatId) {
        this.uniStatId = uniStatId;
    }

    public int getHh() {
        return hh;
    }

    public void setHh(int hh) {
        this.hh = hh;
    }

    public int getHb() {
        return hb;
    }

    public void setHb(int hb) {
        this.hb = hb;
    }

    public int getBe() {
        return be;
    }

    public void setBe(int be) {
        this.be = be;
    }

    public int getMv() {
        return mv;
    }

    public void setMv(int mv) {
        this.mv = mv;
    }

    public int getBb() {
        return bb;
    }

    public void setBb(int bb) {
        this.bb = bb;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public int getBye() {
        return bye;
    }

    public void setBye(int bye) {
        this.bye = bye;
    }

    public int getSl() {
        return sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public int getRp() {
        return rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public int getSh() {
        return sh;
    }

    public void setSh(int sh) {
        this.sh = sh;
    }

    public int getTh() {
        return th;
    }

    public void setTh(int th) {
        this.th = th;
    }

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public int getHe() {
        return he;
    }

    public void setHe(int he) {
        this.he = he;
    }

    public int getBW() {
        return BW;
    }

    public void setBW(int BW) {
        this.BW = BW;
    }

    public int getNW() {
        return NW;
    }

    public void setNW(int NW) {
        this.NW = NW;
    }

    public int getAusland() {
        return ausland;
    }

    public void setAusland(int ausland) {
        this.ausland = ausland;
    }
}
