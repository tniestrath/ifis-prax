package com.analysetool.util;

import com.analysetool.Application;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;

public final class IPHelper {
    private static IPHelper INSTANCE;
    private final static DatabaseReader cityReader;

    static {
        try {
            cityReader = new DatabaseReader.Builder(new File(("backend/src/main/resources/iplocationdbs/city.mmdb"))).withCache(new CHMCache()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private IPHelper() {

    }

    public static IPHelper getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new IPHelper();
        }
        return INSTANCE;
    }


    private static CityResponse getResponse (String ip) {
        CityResponse response = null;
        try {
            response = cityReader.city(InetAddress.getByName(ip));
        } catch (IOException e) {
        } catch (GeoIp2Exception e) {
        }
        return response;
    }

    public static String getCountryName(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getCountry().getName();
        }
        return "";
    }

    public static String getCountryISO(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getCountry().getIsoCode();
        }
        return "";
    }

    public static String getSubName(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getMostSpecificSubdivision().getName();
        }
        return "";
    }

    public static String getSubISO(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getMostSpecificSubdivision().getIsoCode();
        }
        return "";
    }

    public static String getCityName(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getCity().getName();
        }
        return "";
    }

    public static Long getCityNameId(String ip) {
        if(getResponse(ip) != null) {
            return getResponse(ip).getCity().getGeoNameId();
        }
        return 0L;
    }
}
