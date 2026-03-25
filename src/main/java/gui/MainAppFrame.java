/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

/**
 *
 * @author Michael
 */

import dao.OfficerDAO;
import dao.CaseDAO;
import model.Officer;
import model.CaseRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainAppFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainAppFrame.class.getName());

    
    private final OfficerDAO officerDAO = new OfficerDAO();
    private final CaseDAO caseDAO = new CaseDAO();

    
    private Map<Integer, String> categoryIdToName;
    private Map<String, Integer> categoryNameToId;
    private Map<Integer, String> statusIdToName;
    private Map<String, Integer> statusNameToId;
    private Map<Integer, Officer> officerIdToObject; 

    
    private JTextField txtOfficerId, txtOfficerFirstName, txtOfficerLastName, txtOfficerRanks, txtOfficerPhone;
    private JButton btnAddOfficer, btnUpdateOfficer, btnDeleteOfficer, btnClearOfficer;
    private JTable tblOfficers;
    private DefaultTableModel officerTableModel;

    // GUI Components (Case Tab)
    private JTextField txtCaseId, txtCaseDetails, txtCaseLocation, txtReportedOn;
    private JComboBox<String> cmbOfficer, cmbCategory, cmbStatus;
    private JButton btnAddCase, btnUpdateCase, btnDeleteCase, btnClearCase;
    private JTable tblCases;
    private DefaultTableModel caseTableModel;
    
    // Aesthetic Constants
    private final Color TEAL_BUTTON = Color.decode("#00A79D");
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150); // Semi-transparent overlay
    private final Font APP_FONT = new Font("Arial", Font.PLAIN, 14); // Using Arial as SF is proprietary

    /**
     * Main constructor to initialize the GUI.
     */
    public MainAppFrame() { // Renamed from MainAppFrame, now the actual constructor for the JFrame
        setTitle("Crime Case Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized for better fit
        
        // 1. Initialize lookups before building GUI
        initializeLookups();
        
        // 2. Set up the main content pane with the background image
        setupBackgroundPane();
        
        // 3. Create the tabbed pane for Officer and Case management
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(APP_FONT.deriveFont(Font.BOLD, 16));
        tabbedPane.setOpaque(false); // Make tabs transparent to see background
        tabbedPane.setBackground(new Color(0, 0, 0, 0)); 
        tabbedPane.setForeground(Color.WHITE); 
        
        // 4. Create and add tabs
        tabbedPane.addTab("Manage Officers", createOfficerPanel());
        tabbedPane.addTab("Manage Cases", createCasePanel());
        
        // Add tabbed pane to the main frame container (which has the background image)
        // We use a separate panel (mainContent) to center the tabbed pane
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false); // Ensure this panel is also transparent
        mainContent.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainContent.add(tabbedPane, gbc);

        // Add the content to the layered pane
        // Note: getContentPane().getComponent(0) might throw an IndexOutOfBoundsException
        // if the layered pane hasn't been properly initialized. We'll use this simpler approach:
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        
        // Initial setup for bounds, will be resized in componentResized
        mainContent.setBounds(0, 0, 1000, 800); 
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Resize the main content panel to fill the frame whenever the frame resizes
                mainContent.setBounds(0, 0, getWidth(), getHeight());
            }
        });
        
        setVisible(true);
        
        // Initial data load must happen after the frame is visible
        loadOfficerData();
        loadCaseData();
    }
    
    /**
     * Initializes maps for foreign key lookups (Categories, Statuses, Officers).
     */
    private void initializeLookups() {
        categoryIdToName = caseDAO.getAllCategories();
        statusIdToName = caseDAO.getAllStatuses();
        
        // Reverse maps for easy lookup by name
        categoryNameToId = new HashMap<>();
        categoryIdToName.forEach((id, name) -> categoryNameToId.put(name, id));

        statusNameToId = new HashMap<>();
        statusIdToName.forEach((id, name) -> statusNameToId.put(name, id));

        // Officer lookup (ID to Object)
        officerIdToObject = new HashMap<>();
        officerDAO.getAllOfficers().forEach(o -> officerIdToObject.put(o.getOfficerId(), o));
    }
    
    /**
     * Sets up the main content pane with the custom background image and logo.
     */
    private void setupBackgroundPane() {
        // Create a custom content pane that draws the background image
        // JLayeredPane is needed to draw background/logo behind the JTabbedPane
        JLayeredPane layeredPane = new JLayeredPane() {
            // Note: Images must be in the project root directory for this to work easily.
            private final Image background = new ImageIcon("saps_background.jpg").getImage();
            private final Image logo = new ImageIcon("saps_logo.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw background image
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Apply a dark, semi-transparent overlay (for better text readability)
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw logo in the top right corner
                if (logo != null) {
                    int logoWidth = 80;
                    int logoHeight = 80;
                    int x = getWidth() - logoWidth - 20; // 20px padding from right
                    int y = 20; // 20px padding from top
                    g.drawImage(logo, x, y, logoWidth, logoHeight, this);
                }
            }
        };
        layeredPane.setLayout(new BorderLayout());
        setContentPane(layeredPane);
    }
    
    /**
     * Creates a reusable JButton with custom styling.
     * @param text The button text.
     * @param listener The ActionListener.
     * @return The styled JButton.
     */
    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(TEAL_BUTTON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(APP_FONT.deriveFont(Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    /**
     * Creates a standard JTextField with black text and SF-like font.
     */
    private JTextField createStyledTextField(boolean editable) {
        JTextField textField = new JTextField(15);
        textField.setFont(APP_FONT);
        textField.setForeground(Color.BLACK);
        textField.setEditable(editable);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }
    
    /**
     * Creates the panel for managing officers.
     */
    private JPanel createOfficerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false); // Transparent to show background image
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Input Form Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(true); 
        inputPanel.setBackground(new Color(255, 255, 255, 220)); // Semi-transparent white
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Officer Details", 0, 0, APP_FONT.deriveFont(Font.BOLD, 16), Color.BLACK));
        inputPanel.setFont(APP_FONT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Officer ID (Hidden, but used for update/delete)
        txtOfficerId = createStyledTextField(false); 
        txtOfficerId.setText("New/Selected ID");
        txtOfficerId.setVisible(false);
        
        AtomicInteger row = new AtomicInteger(0);
        
        // Helper to add label and field
        Runnable addField = () -> {
            String label = switch (row.get()) {
                case 0 -> "First Name:";
                case 1 -> "Last Name:";
                case 2 -> "Ranks:";
                case 3 -> "Phone (Optional):";
                default -> "";
            };
            
            gbc.gridx = 0;
            gbc.gridy = row.get();
            JLabel lbl = new JLabel(label);
            lbl.setFont(APP_FONT.deriveFont(Font.BOLD));
            inputPanel.add(lbl, gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            inputPanel.add(switch (label) {
                case "First Name:" -> txtOfficerFirstName = createStyledTextField(true);
                case "Last Name:" -> txtOfficerLastName = createStyledTextField(true);
                case "Ranks:" -> txtOfficerRanks = createStyledTextField(true);
                case "Phone (Optional):" -> txtOfficerPhone = createStyledTextField(true);
                default -> new JTextField(); 
            }, gbc);
            row.incrementAndGet();
        };

        addField.run(); // First Name
        addField.run(); // Last Name
        addField.run(); // Ranks
        addField.run(); // Phone (Optional)

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        btnAddOfficer = createStyledButton("Add New Officer", e -> addOfficer());
        btnUpdateOfficer = createStyledButton("Update Selected Officer", e -> updateOfficer());
        btnDeleteOfficer = createStyledButton("Delete Selected Officer", e -> deleteOfficer());
        btnClearOfficer = createStyledButton("Clear Form", e -> clearOfficerForm());

        buttonPanel.add(btnAddOfficer);
        buttonPanel.add(btnUpdateOfficer);
        buttonPanel.add(btnDeleteOfficer);
        buttonPanel.add(btnClearOfficer);

        inputPanel.add(buttonPanel, new GridBagConstraints(0, row.get(), 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 0, 0, 0), 0, 0));

        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] officerColumnNames = {"ID", "First Name", "Last Name", "Ranks", "Phone"};
        officerTableModel = new DefaultTableModel(officerColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Users shouldn't edit the table directly
            }
            @Override // Hide the ID column but keep the data
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : super.getColumnClass(columnIndex);
            }
        };
        tblOfficers = new JTable(officerTableModel);
        tblOfficers.setFont(APP_FONT);
        tblOfficers.getTableHeader().setFont(APP_FONT.deriveFont(Font.BOLD, 15));
        tblOfficers.setRowHeight(25);
        
        // Selection Listener for Update/Delete
        tblOfficers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblOfficers.getSelectedRow() != -1) {
                populateOfficerForm(tblOfficers.getSelectedRow());
            }
        });
        
        // Hide the ID column
        JScrollPane scrollPane = new JScrollPane(tblOfficers);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Hide ID column initially (must be done after table creation)
        SwingUtilities.invokeLater(() -> {
             if (tblOfficers.getColumnModel().getColumnCount() > 0) {
                tblOfficers.getColumnModel().getColumn(0).setMinWidth(0);
                tblOfficers.getColumnModel().getColumn(0).setMaxWidth(0);
                tblOfficers.getColumnModel().getColumn(0).setPreferredWidth(0);
             }
        });


        return panel;
    }

    /**
     * Creates the panel for managing cases.
     */
    private JPanel createCasePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Input Form Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(true);
        inputPanel.setBackground(new Color(255, 255, 255, 220));
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Case Details", 0, 0, APP_FONT.deriveFont(Font.BOLD, 16), Color.BLACK));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCaseId = createStyledTextField(false);
        txtCaseId.setText("New/Selected ID");
        txtCaseId.setVisible(false);

        AtomicInteger row = new AtomicInteger(0);

        // Case Details
        gbc.gridx = 0; gbc.gridy = row.get(); gbc.gridwidth = 1;
        JLabel lblDetails = new JLabel("Details:");
        lblDetails.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblDetails, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtCaseDetails = createStyledTextField(true);
        inputPanel.add(txtCaseDetails, gbc);
        row.incrementAndGet();

        // Location and Reported On
        gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = row.get(); 
        JLabel lblLocation = new JLabel("Location:");
        lblLocation.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblLocation, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        txtCaseLocation = createStyledTextField(true);
        inputPanel.add(txtCaseLocation, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.0;
        JLabel lblReported = new JLabel("Reported Date (YYYY-MM-DD):");
        lblReported.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblReported, gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        txtReportedOn = createStyledTextField(true);
        txtReportedOn.setText(LocalDate.now().toString());
        inputPanel.add(txtReportedOn, gbc);
        row.incrementAndGet();
        
        // Officer, Category, Status
        gbc.gridwidth = 1; gbc.weightx = 0.0;
        gbc.gridx = 0; gbc.gridy = row.get();
        JLabel lblOfficer = new JLabel("Officer:");
        lblOfficer.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblOfficer, gbc);
        gbc.gridx = 1; gbc.weightx = 0.33;
        cmbOfficer = new JComboBox<>(officerIdToObject.values().stream().map(Officer::toString).toArray(String[]::new));
        cmbOfficer.setFont(APP_FONT);
        inputPanel.add(cmbOfficer, gbc);

        gbc.gridx = 2; gbc.weightx = 0.0;
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblCategory, gbc);
        gbc.gridx = 3; gbc.weightx = 0.33;
        cmbCategory = new JComboBox<>(categoryNameToId.keySet().toArray(String[]::new));
        cmbCategory.setFont(APP_FONT);
        inputPanel.add(cmbCategory, gbc);

        gbc.gridx = 4; gbc.weightx = 0.0;
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(APP_FONT.deriveFont(Font.BOLD));
        inputPanel.add(lblStatus, gbc);
        gbc.gridx = 5; gbc.weightx = 0.33;
        cmbStatus = new JComboBox<>(statusNameToId.keySet().toArray(String[]::new));
        cmbStatus.setFont(APP_FONT);
        inputPanel.add(cmbStatus, gbc);
        row.incrementAndGet();

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        btnAddCase = createStyledButton("Add New Case", e -> addCase());
        btnUpdateCase = createStyledButton("Update Selected Case", e -> updateCase());
        btnDeleteCase = createStyledButton("Delete Selected Case", e -> deleteCase());
        btnClearCase = createStyledButton("Clear Form", e -> clearCaseForm());

        buttonPanel.add(btnAddCase);
        buttonPanel.add(btnUpdateCase);
        buttonPanel.add(btnDeleteCase);
        buttonPanel.add(btnClearCase);

        gbc.gridx = 0; gbc.gridy = row.get(); gbc.gridwidth = 6; gbc.weightx = 1.0;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        // ID, Details, Location, ReportedOn, OfficerID_FK, OfficerName, CategoryID_FK, CategoryName, StatusDescription
        String[] caseColumnNames = {"ID", "Details", "Location", "Reported On", "Officer ID", "Officer Name", "Category ID", "Category", "Status"};
        caseTableModel = new DefaultTableModel(caseColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
            @Override // Hide the ID columns
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 4 || columnIndex == 6) ? Integer.class : super.getColumnClass(columnIndex);
            }
        };
        tblCases = new JTable(caseTableModel);
        tblCases.setFont(APP_FONT);
        tblCases.getTableHeader().setFont(APP_FONT.deriveFont(Font.BOLD, 15));
        tblCases.setRowHeight(25);
        
        // Selection Listener for Update/Delete
        tblCases.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCases.getSelectedRow() != -1) {
                populateCaseForm(tblCases.getSelectedRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblCases);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Hide FK/ID columns
        SwingUtilities.invokeLater(() -> {
            if (tblCases.getColumnModel().getColumnCount() > 6) {
                int[] hiddenColumns = {0, 4, 6}; // CaseID, OfficerID_FK, CategoryID_FK
                for (int col : hiddenColumns) {
                    tblCases.getColumnModel().getColumn(col).setMinWidth(0);
                    tblCases.getColumnModel().getColumn(col).setMaxWidth(0);
                    tblCases.getColumnModel().getColumn(col).setPreferredWidth(0);
                }
            }
        });

        return panel;
    }
    
    // =========================================================================
    //                            OFFICER LOGIC METHODS
    // =========================================================================

    /**
     * Loads officer data from the DAO into the JTable.
     */
    private void loadOfficerData() {
        officerTableModel.setRowCount(0); // Clear existing data
        List<Officer> officers = officerDAO.getAllOfficers();
        officerIdToObject.clear();
        
        for (Officer officer : officers) {
            officerTableModel.addRow(new Object[]{
                officer.getOfficerId(),
                officer.getFirstName(),
                officer.getLastName(),
                officer.getRanks(), // Corrected to use getRanks()
                officer.getPhone()
            });
            // Update the lookup map
            officerIdToObject.put(officer.getOfficerId(), officer);
        }
        
        // Re-populate the Case Officer ComboBox
        populateCaseOfficerComboBox();
    }
    
    
    private void populateOfficerForm(int rowIndex) {
        // Data is pulled directly from the model
        txtOfficerId.setText(officerTableModel.getValueAt(rowIndex, 0).toString());
        txtOfficerFirstName.setText(officerTableModel.getValueAt(rowIndex, 1).toString());
        txtOfficerLastName.setText(officerTableModel.getValueAt(rowIndex, 2).toString());
        txtOfficerRanks.setText(officerTableModel.getValueAt(rowIndex, 3).toString());
        txtOfficerPhone.setText(officerTableModel.getValueAt(rowIndex, 4).toString());
    }
    
    
    private void clearOfficerForm() {
        txtOfficerId.setText("New/Selected ID");
        txtOfficerFirstName.setText("");
        txtOfficerLastName.setText("");
        txtOfficerRanks.setText("");
        txtOfficerPhone.setText("");
        tblOfficers.clearSelection();
    }

    /**
     * Adds a new officer record.
     */
    private void addOfficer() {
        // Exception Handling: Basic input validation
        if (txtOfficerFirstName.getText().isEmpty() || txtOfficerLastName.getText().isEmpty() || txtOfficerRanks.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in First Name, Last Name, and Ranks.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Officer newOfficer = new Officer(
                txtOfficerFirstName.getText(),
                txtOfficerLastName.getText(),
                txtOfficerRanks.getText(),
                txtOfficerPhone.getText()
            );
            
            int newId = officerDAO.insertOfficer(newOfficer);
            
            if (newId != -1) {
                JOptionPane.showMessageDialog(this, "Officer added successfully! ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfficerData(); // Refresh table
                clearOfficerForm(); // Clear form
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add officer due to a database error.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception during officer insertion.", ex);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the selected officer record.
     */
    private void updateOfficer() {
        if (tblOfficers.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select an officer from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Exception Handling: Basic input validation
        if (txtOfficerFirstName.getText().isEmpty() || txtOfficerLastName.getText().isEmpty() || txtOfficerRanks.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(txtOfficerId.getText());
            Officer updatedOfficer = new Officer(
                id,
                txtOfficerFirstName.getText(),
                txtOfficerLastName.getText(),
                txtOfficerRanks.getText(),
                txtOfficerPhone.getText()
            );
            
            if (officerDAO.updateOfficer(updatedOfficer)) {
                JOptionPane.showMessageDialog(this, "Officer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfficerData();
                clearOfficerForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update officer. Record may not exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "Invalid ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception during officer update.", ex);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected officer record.
     */
    private void deleteOfficer() {
        if (tblOfficers.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select an officer from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this officer? (ID: " + txtOfficerId.getText() + ")", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idToDelete = Integer.parseInt(txtOfficerId.getText());
                if (officerDAO.deleteOfficer(idToDelete)) {
                    JOptionPane.showMessageDialog(this, "Officer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOfficerData();
                    clearOfficerForm();
                    loadCaseData(); // Cases might have been deleted/affected, refresh case table as well
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete officer. This is often because the officer is still assigned to one or more cases. Please re-assign or delete those cases first.", 
                        "Deletion Error (Foreign Key)", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(this, "Invalid ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Exception during officer deletion.", ex);
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // =========================================================================
    //                            CASE LOGIC METHODS
    // =========================================================================
    
    /**
     * Updates the Case Officer ComboBox whenever officer data changes.
     */
    private void populateCaseOfficerComboBox() {
        cmbOfficer.removeAllItems();
        // Check if map is not null and has content before streaming
        if (officerIdToObject != null && !officerIdToObject.isEmpty()) {
            officerIdToObject.values().stream().map(Officer::toString).forEach(cmbOfficer::addItem);
        }
    }
    
    /**
     * Loads case data from the DAO into the JTable.
     */
    private void loadCaseData() {
        caseTableModel.setRowCount(0); // Clear existing data
        List<Object[]> caseData = caseDAO.getAllCaseData();
        
        for (Object[] row : caseData) {
            // Note: The row structure matches the columns in the model: 
            // ID, Details, Location, ReportedOn, OfficerID_FK, OfficerName, CategoryID_FK, CategoryName, StatusDescription
            caseTableModel.addRow(row);
        }
    }
    
    /**
     * Populates the case form fields when a row is selected.
     * @param rowIndex The index of the selected row.
     */
    private void populateCaseForm(int rowIndex) {
        // Data is pulled directly from the model
        txtCaseId.setText(caseTableModel.getValueAt(rowIndex, 0).toString());
        txtCaseDetails.setText(caseTableModel.getValueAt(rowIndex, 1).toString());
        txtCaseLocation.setText(caseTableModel.getValueAt(rowIndex, 2).toString());
        
        // Date is retrieved as java.sql.Date, format it for the text field
        java.sql.Date sqlDate = (java.sql.Date) caseTableModel.getValueAt(rowIndex, 3);
        txtReportedOn.setText(sqlDate.toLocalDate().toString());
        
        // Foreign Key Lookups (set ComboBoxes)
        String categoryName = (String) caseTableModel.getValueAt(rowIndex, 7);
        String statusName = (String) caseTableModel.getValueAt(rowIndex, 8);
        
        // Find the full officer string (OfficerID - FirstName LastName (Ranks))
        int officerIdFk = (int) caseTableModel.getValueAt(rowIndex, 4);
        Officer officerObj = officerIdToObject.get(officerIdFk);
        if (officerObj != null) {
            cmbOfficer.setSelectedItem(officerObj.toString());
        }
        
        cmbCategory.setSelectedItem(categoryName);
        cmbStatus.setSelectedItem(statusName);
    }

    
    private void clearCaseForm() {
        txtCaseId.setText("New/Selected ID");
        txtCaseDetails.setText("");
        txtCaseLocation.setText("");
        txtReportedOn.setText(LocalDate.now().toString());
        if (cmbOfficer.getItemCount() > 0) cmbOfficer.setSelectedIndex(0);
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        if (cmbStatus.getItemCount() > 0) cmbStatus.setSelectedIndex(0);
        
        tblCases.clearSelection();
    }
    
    
    private int getIdFromComboBox(JComboBox<String> cmb, boolean isOfficer) {
        String selectedItem = (String) cmb.getSelectedItem();
        if (selectedItem == null || selectedItem.isEmpty()) {
            throw new IllegalArgumentException("No item selected in ComboBox. Ensure the database lookups were successful.");
        }
        
        if (isOfficer) {
            // Officer string format: "ID - FirstName LastName (Ranks)"
            try {
                // Officer ID is the first part before the space/dash
                String idStr = selectedItem.split(" - ")[0];
                return Integer.parseInt(idStr.trim());
            } catch (Exception e) {
                 LOGGER.log(Level.SEVERE, "Error parsing officer ID from ComboBox string: " + selectedItem, e);
                 throw new IllegalArgumentException("Invalid Officer format selected.");
            }
        } else if (cmb == cmbCategory) {
            return categoryNameToId.getOrDefault(selectedItem, -1);
        } else if (cmb == cmbStatus) {
            return statusNameToId.getOrDefault(selectedItem, -1);
        }
        return -1; 
    }

    /**
     * Adds a new case record.
     */
    private void addCase() {
        // Exception Handling: Input validation
        if (txtCaseDetails.getText().isEmpty() || txtCaseLocation.getText().isEmpty() || txtReportedOn.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required case details.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int officerId = getIdFromComboBox(cmbOfficer, true);
            int categoryId = getIdFromComboBox(cmbCategory, false);
            int statusId = getIdFromComboBox(cmbStatus, false);
            LocalDate reportedDate = LocalDate.parse(txtReportedOn.getText());

            if (officerId == -1 || categoryId == -1 || statusId == -1) {
                 JOptionPane.showMessageDialog(this, "Invalid selection for Officer, Category, or Status.", "Data Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            CaseRecord newCase = new CaseRecord(
                txtCaseDetails.getText(),
                officerId,
                categoryId,
                txtCaseLocation.getText(),
                reportedDate,
                statusId
            );

            int newId = caseDAO.insertCase(newCase);

            if (newId != -1) {
                JOptionPane.showMessageDialog(this, "Case added successfully! Case ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCaseData();
                clearCaseForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add case due to a database error.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Reported Date format is incorrect. Use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception during case insertion.", ex);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Updates the selected case record.
     */
    private void updateCase() {
        if (tblCases.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtCaseDetails.getText().isEmpty() || txtCaseLocation.getText().isEmpty() || txtReportedOn.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int caseId = Integer.parseInt(txtCaseId.getText());
            int officerId = getIdFromComboBox(cmbOfficer, true);
            int categoryId = getIdFromComboBox(cmbCategory, false);
            int statusId = getIdFromComboBox(cmbStatus, false);
            LocalDate reportedDate = LocalDate.parse(txtReportedOn.getText());

            if (officerId == -1 || categoryId == -1 || statusId == -1) {
                 JOptionPane.showMessageDialog(this, "Invalid selection for Officer, Category, or Status.", "Data Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            CaseRecord updatedCase = new CaseRecord(
                caseId,
                txtCaseDetails.getText(),
                officerId,
                categoryId,
                txtCaseLocation.getText(),
                reportedDate,
                statusId
            );

            if (caseDAO.updateCase(updatedCase)) {
                JOptionPane.showMessageDialog(this, "Case updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCaseData();
                clearCaseForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update case. Record may not exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "Invalid ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Reported Date format is incorrect. Use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception during case update.", ex);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void deleteCase() {
        if (tblCases.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this case? (ID: " + txtCaseId.getText() + ")", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int idToDelete = Integer.parseInt(txtCaseId.getText());
                if (caseDAO.deleteCase(idToDelete)) {
                    JOptionPane.showMessageDialog(this, "Case deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCaseData();
                    clearCaseForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete case. Record may not exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(this, "Invalid ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Exception during case deletion.", ex);
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
}
