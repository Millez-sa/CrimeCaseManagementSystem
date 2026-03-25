/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

/**
 *
 * @author Michael
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/crime_management?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Password";

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);

            
            LOGGER.log(Level.INFO, "Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            LOGGER.log(Level.INFO, "Database connection successful.");
        } catch (SQLException se) {
            LOGGER.log(Level.SEVERE, "SQL Exception during connection attempt.", se);
            System.err.println("Database connection failed! Check if MySQL is running and credentials are correct.");
            System.err.println("Error details: " + se.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL Driver not found.", e);
            System.err.println("MySQL JDBC Driver not found. Ensure the connector JAR is in your project libraries.");
        }
        return conn;
    }
}
