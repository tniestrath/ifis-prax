import com.analysetool.Application;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public final class IPHelper {
    private static IPHelper INSTANCE;
    private final static DatabaseReader cityReader;

    static {
        try {
            cityReader = new DatabaseReader.Builder(new File(Application.class.getClassLoader().getResource("iplocationdbs/city.mmdb").toURI())).withCache(new CHMCache()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
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
        Country country = getResponse(ip).getCountry();
        return country.getName();
    }

    public static String getCountryISO(String ip) {
        Country country = getResponse(ip).getCountry();
        return country.getIsoCode();
    }

    public static String getSubName(String ip) {
        Subdivision sub = getResponse(ip).getMostSpecificSubdivision();
        return sub.getName();
    }

    public static String getSubISO(String ip) {
        Subdivision sub = getResponse(ip).getMostSpecificSubdivision();
        return sub.getIsoCode();
    }

    public static String getCityName(String ip) {
        City city = getResponse(ip).getCity();
        return city.getName();
    }

    public static Long getCityNameId(String ip) {
        City city = getResponse(ip).getCity();
        return city.getGeoNameId();
    }
}
