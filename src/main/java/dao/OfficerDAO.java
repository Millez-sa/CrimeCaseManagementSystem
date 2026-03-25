/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao; 

/**
 *
 * @author Michael
 */

import db.DatabaseConnection;
import model.Officer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OfficerDAO {
    private static final Logger LOGGER = Logger.getLogger(OfficerDAO.class.getName());

    private static final String INSERT_OFFICER = "INSERT INTO OFFICER (FirstName, LastName, Ranks, Phone) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_OFFICERS = "SELECT * FROM OFFICER ORDER BY LastName";
    private static final String UPDATE_OFFICER = "UPDATE OFFICER SET FirstName=?, LastName=?, Ranks=?, Phone=? WHERE OfficerID=?";
    private static final String DELETE_OFFICER = "DELETE FROM OFFICER WHERE OfficerID=?";

    
    public int insertOfficer(Officer officer) {
        int generatedId = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_OFFICER, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, officer.getFirstName());
            pstmt.setString(2, officer.getLastName());
            pstmt.setString(3, officer.getRanks());
            pstmt.setString(4, officer.getPhone());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        officer.setOfficerId(generatedId);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error inserting officer: " + officer, ex);
        }
        return generatedId;
    }

    public List<Officer> getAllOfficers() {
        List<Officer> officers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_OFFICERS)) {

            while (rs.next()) {
                int id = rs.getInt("OfficerID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String ranks = rs.getString("Ranks");
                String phone = rs.getString("Phone");
                officers.add(new Officer(id, firstName, lastName, ranks, phone));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving all officers.", ex);
        }
        return officers;
    }
    
    public Officer getOfficerById(int officerId) {
        String sql = "SELECT * FROM OFFICER WHERE OfficerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, officerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Officer(
                        rs.getInt("OfficerID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Ranks"),
                        rs.getString("Phone")
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving officer by ID: " + officerId, ex);
        }
        return null; 
    }

   
    public boolean updateOfficer(Officer officer) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_OFFICER)) {

            pstmt.setString(1, officer.getFirstName());
            pstmt.setString(2, officer.getLastName());
            pstmt.setString(3, officer.getRanks());
            pstmt.setString(4, officer.getPhone());
            pstmt.setInt(5, officer.getOfficerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating officer: " + officer.getOfficerId(), ex);
            return false;
        }
    }

    
    public boolean deleteOfficer(int officerId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_OFFICER)) {

            pstmt.setInt(1, officerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                LOGGER.log(Level.WARNING, "Cannot delete officer {0}: Foreign key constraint violation.", officerId);
                return false; 
            }
            LOGGER.log(Level.SEVERE, "Error deleting officer: " + officerId, ex);
            return false;
        }
    }
}
