/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Faranani Matsa
 */

import java.time.LocalDate;


public class CaseRecord {
    private int caseId;
    private String caseDetails;
    private int officerIdFk;     
    private int categoryIdFk;     
    private String location;
    private LocalDate reportedOn;
    private int statusFk;         

       public CaseRecord(int caseId, String caseDetails, int officerIdFk, int categoryIdFk, String location, LocalDate reportedOn, int statusFk) {
        this.caseId = caseId;
        this.caseDetails = caseDetails;
        this.officerIdFk = officerIdFk;
        this.categoryIdFk = categoryIdFk;
        this.location = location;
        this.reportedOn = reportedOn;
        this.statusFk = statusFk;
    }

    public CaseRecord(String caseDetails, int officerIdFk, int categoryIdFk, String location, LocalDate reportedOn, int statusFk) {
        this.caseDetails = caseDetails;
        this.officerIdFk = officerIdFk;
        this.categoryIdFk = categoryIdFk;
        this.location = location;
        this.reportedOn = reportedOn;
        this.statusFk = statusFk;
    }

    
    public int getCaseId() { return caseId; }
    public String getCaseDetails() { return caseDetails; }
    public int getOfficerIdFk() { return officerIdFk; }
    public int getCategoryIdFk() { return categoryIdFk; }
    public String getLocation() { return location; }
    public LocalDate getReportedOn() { return reportedOn; }
    public int getStatusFk() { return statusFk; }

    public void setCaseId(int caseId) { this.caseId = caseId; }
    public void setCaseDetails(String caseDetails) { this.caseDetails = caseDetails; }
    public void setOfficerIdFk(int officerIdFk) { this.officerIdFk = officerIdFk; }
    public void setCategoryIdFk(int categoryIdFk) { this.categoryIdFk = categoryIdFk; }
    public void setLocation(String location) { this.location = location; }
    public void setReportedOn(LocalDate reportedOn) { this.reportedOn = reportedOn; }
    public void setStatusFk(int statusFk) { this.statusFk = statusFk; }
    
    @Override
    public String toString() {
        return caseId + ": " + caseDetails + " @ " + location;
    }
}
