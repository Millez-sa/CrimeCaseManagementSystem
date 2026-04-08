/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao; 

/**
 *
 * @author Faranani Matsa
 */

import db.DatabaseConnection;
import model.CaseRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CaseDAO {
    private static final Logger LOGGER = Logger.getLogger(CaseDAO.class.getName());

    private static final String INSERT_CASES = "INSERT INTO `CASES` (CaseDetails, OfficerID_FK, CategoryID_FK, Location, ReportedOn, Status_FK) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_ALL_CASES = 
        "SELECT c.CaseID, c.CaseDetails, c.Location, c.ReportedOn, " +
        "   o.OfficerID, o.FirstName, o.LastName, " +
        "   cat.TypeID, cat.CategoryName, " +
        "   stat.StatusCode, stat.StatusDescription " +
        "FROM `CASES` c " +
        "JOIN OFFICER o ON c.OfficerID_FK = o.OfficerID " +
        "JOIN CrimeCategory cat ON c.CategoryID_FK = cat.TypeID " +
        "JOIN CaseStatus stat ON c.Status_FK = stat.StatusCode " +
        "ORDER BY c.ReportedOn DESC";
    
    private static final String UPDATE_CASES = "UPDATE `CASES` SET CaseDetails=?, OfficerID_FK=?, CategoryID_FK=?, Location=?, ReportedOn=?, Status_FK=? WHERE CaseID=?";
    private static final String DELETE_CASES = "DELETE FROM `CASES` WHERE CaseID=?";

    private static final String SELECT_CATEGORIES = "SELECT TypeID, CategoryName FROM CrimeCategory";
    private static final String SELECT_STATUSES = "SELECT StatusCode, StatusDescription FROM CaseStatus";


    
    public int insertCase(CaseRecord caseRecord) {
        int generatedId = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CASES, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, caseRecord.getCaseDetails());
            pstmt.setInt(2, caseRecord.getOfficerIdFk());
            pstmt.setInt(3, caseRecord.getCategoryIdFk());
            pstmt.setString(4, caseRecord.getLocation());
            pstmt.setDate(5, Date.valueOf(caseRecord.getReportedOn()));
            pstmt.setInt(6, caseRecord.getStatusFk());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        caseRecord.setCaseId(generatedId);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error inserting case: " + caseRecord.getCaseDetails(), ex);
        }
        return generatedId;
    }

    
    public List<Object[]> getAllCaseData() {
        List<Object[]> caseDataList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CASES)) {

            while (rs.next()) {
                Object[] row = new Object[9]; 
                row[0] = rs.getInt("CaseID"); 
                row[1] = rs.getString("CaseDetails");
                row[2] = rs.getString("Location");
                row[3] = rs.getDate("ReportedOn");
                
                row[4] = rs.getInt("OfficerID"); 
                row[5] = rs.getString("FirstName") + " " + rs.getString("LastName");
               
                row[6] = rs.getInt("TypeID");
                row[7] = rs.getString("CategoryName");
                row[8] = rs.getString("StatusDescription"); 
               
                caseDataList.add(row);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving all case data.", ex);
        }
        return caseDataList;
    }

    
    public boolean updateCase(CaseRecord caseRecord) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CASES)) {

            pstmt.setString(1, caseRecord.getCaseDetails());
            pstmt.setInt(2, caseRecord.getOfficerIdFk());
            pstmt.setInt(3, caseRecord.getCategoryIdFk());
            pstmt.setString(4, caseRecord.getLocation());
            pstmt.setDate(5, Date.valueOf(caseRecord.getReportedOn()));
            pstmt.setInt(6, caseRecord.getStatusFk());
            pstmt.setInt(7, caseRecord.getCaseId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating case: " + caseRecord.getCaseId(), ex);
            return false;
        }
    }

    
    public boolean deleteCase(int caseId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_CASES)) {

            pstmt.setInt(1, caseId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting case: " + caseId, ex);
            return false;
        }
    }

    public Map<Integer, String> getAllCategories() {
        Map<Integer, String> categories = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_CATEGORIES)) {

            while (rs.next()) {
                categories.put(rs.getInt("TypeID"), rs.getString("CategoryName"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving crime categories.", ex);
        }
        return categories;
    }

    
    public Map<Integer, String> getAllStatuses() {
        Map<Integer, String> statuses = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_STATUSES)) {

            while (rs.next()) {
                statuses.put(rs.getInt("StatusCode"), rs.getString("StatusDescription"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving case statuses.", ex);
        }
        return statuses;
    }
}
