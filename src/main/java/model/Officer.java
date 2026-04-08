/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Faranani Matsa
 */
public class Officer {
    private int officerId;
    private String firstName;
    private String lastName;
    private String ranks;
    private String phone;

    
    public Officer(String firstName, String lastName, String ranks, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ranks = ranks;
        this.phone = phone;
    }

    public Officer(int officerId, String firstName, String lastName, String ranks, String phone) {
        this.officerId = officerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ranks = ranks;
        this.phone = phone;
    }
    
  
    public int getOfficerId() {
        return officerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRanks() {
        return ranks;
    }

    public String getPhone() {
        return phone;
    }
    
    
    public void setOfficerId(int officerId) {
        this.officerId = officerId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRanks(String ranks) {
        this.ranks = ranks;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public String toString() {
        return officerId + " - " + firstName + " " + lastName + " (" + ranks + ")";
    }
}
