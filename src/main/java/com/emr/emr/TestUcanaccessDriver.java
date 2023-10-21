package com.emr.emr;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestUcanaccessDriver {
    public static void main(String[] args) {
        try {
            // Load the UCanAccess driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // Specify the path of your Access database file
            String dbUrl = TestUcanaccessDriver.class.getClassLoader().getResource("com/emr/emr/Logins.accdb").getPath();

            // Establish a connection
            String jdbcUrl = "jdbc:ucanaccess://" + dbUrl;
            Connection connection = DriverManager.getConnection(jdbcUrl);

            // If the connection is successful, print a success message
            System.out.println("Connected successfully!");

            // Don't forget to close the connection
            connection.close();
        } catch (Exception e) {
            // If there's an error, print an error message
            System.out.println("Error: " + e.getMessage());
        }
    }
}
