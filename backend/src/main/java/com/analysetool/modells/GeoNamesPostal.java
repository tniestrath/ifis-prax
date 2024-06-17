package com.analysetool.modells;

import jakarta.persistence.*;

@Entity
@Table(name = "\"wp_geonamesPostal\"")
public class GeoNamesPostal {

    @Id
    @Column(name = "idwpgnp")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="country_code")
    private String countryCode;

    @Column(name="postal_code")
    private String postalCode;

    @Column(name="place_name")
    private String placeName;

    @Column(name="admin1_name")
    private String admin1Name;

    @Column(name="admin1_code")
    private String admin1Code;

    @Column(name="admin2_name")
    private String admin2Name;

    @Column(name="admin2_code")
    private String admin2Code;

    @Column(name="admin3_name")
    private String admin3Name;

    @Column(name="admin3_code")
    private String admin3Code;

    @Column(name="latitude")
    private double latitude;

    @Column(name="longitude")
    private double longitude;

    @Column(name="accuracy")
    private int accuracy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAdmin1Name() {
        return admin1Name;
    }

    public void setAdmin1Name(String admin1Name) {
        this.admin1Name = admin1Name;
    }

    public String getAdmin1Code() {
        return admin1Code;
    }

    public void setAdmin1Code(String admin1Code) {
        this.admin1Code = admin1Code;
    }

    public String getAdmin2Name() {
        return admin2Name;
    }

    public void setAdmin2Name(String admin2Name) {
        this.admin2Name = admin2Name;
    }

    public String getAdmin2Code() {
        return admin2Code;
    }

    public void setAdmin2Code(String admin2Code) {
        this.admin2Code = admin2Code;
    }

    public String getAdmin3Name() {
        return admin3Name;
    }

    public void setAdmin3Name(String admin3Name) {
        this.admin3Name = admin3Name;
    }

    public String getAdmin3Code() {
        return admin3Code;
    }

    public void setAdmin3Code(String admin3Code) {
        this.admin3Code = admin3Code;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }
}
