package com.analysetool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import com.analysetool.api.CompanyController;
import com.analysetool.modells.Company;
import com.analysetool.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class DataReader {
    static String url = "jdbc:mysql://localhost:3306/companies"; // Replace 'databasename' with the name of your database
    static String user = "user"; // Replace 'username' with your MySQL username
    static String password = "password"; // Replace 'password' with your MySQL password



    public static void dataindb(ApplicationContext context) {

        //@Autowired
        CompanyService cc;
        try
        {Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (Exception e){e.printStackTrace();}
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connection to MySQL database has been established.");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM companies.tx_providers_domain_model_provider");
            boolean b = rs.next();
            while (b) {
                String name = rs.getString("name");
                String website = rs.getString("website");
                String keywords= rs.getString("keywords");
                String country= rs.getString("country");
                String street= rs.getString("street");
                String email= rs.getString("email");
                String phone= rs.getString("phone");
                String contactp= rs.getString("contact_person");
                String manager= rs.getString("manager");
                System.out.println( ", Name: " + name + ", Website: " + website);
                cc = context.getBean(CompanyService.class);
                cc.save(new Company(name,new Company.Kontaktdaten(email,phone,website),"","",country,"",keywords));
                b= rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Connection to MySQL database failed: " + e.getMessage());

        }
    }
}
