/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.crimecasemanagementsystem;

/**
 *
 * @author Michael
 */

import gui.MainAppFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class CrimeCaseManagementSystem {

    public static void main(String[] args) {
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            
        }
        SwingUtilities.invokeLater(MainAppFrame::new);
    }
}