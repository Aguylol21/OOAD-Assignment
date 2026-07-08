package view;

import facade.RentalFacade;
import model.Bill;
import model.Equipment;
import model.EquipmentCategoryConstants;
import model.Rental;
import model.RentalStatus;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RentalPanel extends JPanel {

    private RentalFacade rentalFacade;

    private JTextField userIdField;
    private JTextField userNameField;
    private JTextField equipmentSearchField;
    private JTextField durationField;

    private JComboBox<String> userTypeComboBox;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> availableEquipmentComboBox;
    private JComboBox<String> activeRentalComboBox;
    private JCheckBox finalYearCheckBox;
    private JCheckBox returnDamagedCheckBox;
    private JTextArea returnRentalDetailsArea;
    private boolean loadingActiveRentals;

    private JTable rentalHistoryTable;
    private DefaultTableModel rentalHistoryTableModel;

    public RentalPanel(RentalFacade rentalFacade) {
        this.rentalFacade = rentalFacade;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Rent Equipment", createRentPanel());
        tabbedPane.addTab("Return Equipment", createReturnPanel());
        tabbedPane.addTab("Rental History", createRentalHistoryPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadAvailableEquipment();
        loadActiveRentals();
        loadRentalHistory();
    }

    private JPanel createRentPanel() {
        JPanel rentPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Rent Equipment"));

        userIdField = new JTextField();
        userNameField = new JTextField();
        durationField = new JTextField();
        userTypeComboBox = new JComboBox<>(new String[]{
                "Student",
                "Staff"
        });
        finalYearCheckBox = new JCheckBox("Final Year");
        categoryComboBox = new JComboBox<>(EquipmentCategoryConstants.getAllCategories());
        equipmentSearchField = new JTextField();
        availableEquipmentComboBox = new JComboBox<>();

        formPanel.add(new JLabel("User ID:"));
        formPanel.add(userIdField);

        formPanel.add(new JLabel("User Name:"));
        formPanel.add(userNameField);

        formPanel.add(new JLabel("User Type:"));
        formPanel.add(userTypeComboBox);

        formPanel.add(new JLabel("Student Status:"));
        formPanel.add(finalYearCheckBox);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);

        formPanel.add(new JLabel("Search Equipment:"));
        formPanel.add(equipmentSearchField);

        formPanel.add(new JLabel("Available Equipment:"));
        formPanel.add(availableEquipmentComboBox);

        formPanel.add(new JLabel("Duration (Days):"));
        formPanel.add(durationField);

        JButton rentButton = new JButton("Rent Equipment");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(rentButton);
        buttonPanel.add(refreshButton);

        rentPanel.add(formPanel, BorderLayout.NORTH);
        rentPanel.add(buttonPanel, BorderLayout.CENTER);

        rentButton.addActionListener(e -> rentEquipment());
        refreshButton.addActionListener(e -> refreshRentalData());
        categoryComboBox.addActionListener(e -> loadAvailableEquipment());
        userTypeComboBox.addActionListener(e -> updateFinalYearCheckBox());
        equipmentSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                loadAvailableEquipment();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                loadAvailableEquipment();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                loadAvailableEquipment();
            }
        });

        updateFinalYearCheckBox();

        return rentPanel;
    }

    private JPanel createReturnPanel() {
        JPanel returnPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Return Equipment"));

        activeRentalComboBox = new JComboBox<>();
        returnDamagedCheckBox = new JCheckBox("Damaged Equipment");
        returnRentalDetailsArea = new JTextArea(9, 40);
        returnRentalDetailsArea.setEditable(false);

        formPanel.add(new JLabel("Active Rental:"));
        formPanel.add(activeRentalComboBox);
        formPanel.add(new JLabel("Damaged Equipment:"));
        formPanel.add(returnDamagedCheckBox);

        JScrollPane detailsScrollPane = new JScrollPane(returnRentalDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Rental Details"));

        JButton returnButton = new JButton("Return Equipment");
        JButton returnAndBillButton = new JButton("Return & Generate Bill");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(returnButton);
        buttonPanel.add(returnAndBillButton);
        buttonPanel.add(refreshButton);

        returnPanel.add(formPanel, BorderLayout.NORTH);
        returnPanel.add(detailsScrollPane, BorderLayout.CENTER);
        returnPanel.add(buttonPanel, BorderLayout.SOUTH);

        returnButton.addActionListener(e -> returnEquipment());
        returnAndBillButton.addActionListener(e -> returnAndGenerateBill());
        refreshButton.addActionListener(e -> refreshRentalData());
        activeRentalComboBox.addActionListener(e -> updateReturnRentalDetails());

        return returnPanel;
    }

    private JPanel createRentalHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());

        String[] columns = {
                "Rental ID",
                "User",
                "Equipment",
                "Rental Date",
                "Expected Return",
                "Actual Return",
                "Status",
                "Total Fee",
                "Availability"
        };

        rentalHistoryTableModel = new DefaultTableModel(columns, 0);
        rentalHistoryTable = new JTable(rentalHistoryTableModel);

        JScrollPane scrollPane = new JScrollPane(rentalHistoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rental History"));

        JButton refreshButton = new JButton("Refresh");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);

        historyPanel.add(scrollPane, BorderLayout.CENTER);
        historyPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshRentalData());

        return historyPanel;
    }

    private void rentEquipment() {
        try {
            String userId = userIdField.getText().trim();
            String userName = userNameField.getText().trim();
            String userType = getSelectedUserType();
            String equipmentId = getSelectedId(availableEquipmentComboBox);
            int durationDays = Integer.parseInt(durationField.getText().trim());

            Rental rental = rentalFacade.rentEquipment(
                    userId,
                    userName,
                    userType,
                    equipmentId,
                    durationDays
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Equipment rented successfully. Rental ID: " + rental.getRentalId()
            );

            clearRentFields();
            refreshRentalData();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Duration must be a valid number.");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while renting equipment.");
            e.printStackTrace();
        }
    }

    private void returnEquipment() {
        try {
            String rentalId = getSelectedId(activeRentalComboBox);

            Rental rental = rentalFacade.returnEquipment(rentalId);

            JOptionPane.showMessageDialog(
                    this,
                    "Equipment returned successfully. Rental ID: " + rental.getRentalId()
            );

            refreshRentalData();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while returning equipment.");
            e.printStackTrace();
        }
    }

    private void returnAndGenerateBill() {
        try {
            String rentalId = getSelectedId(activeRentalComboBox);
            boolean damaged = returnDamagedCheckBox.isSelected();

            Rental rental = rentalFacade.returnEquipment(rentalId);
            Bill bill = rentalFacade.calculateBill(rentalId, damaged);

            JTextArea billTextArea = new JTextArea(bill.generateBillText());
            billTextArea.setEditable(false);

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(billTextArea),
                    "Equipment Returned. Rental ID: " + rental.getRentalId(),
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshRentalData();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while returning equipment and generating bill.");
            e.printStackTrace();
        }
    }

    private void loadAvailableEquipment() {
        try {
            availableEquipmentComboBox.removeAllItems();

            String selectedCategory = categoryComboBox.getSelectedItem().toString();
            String searchText = equipmentSearchField.getText().trim().toLowerCase();
            List<Equipment> equipmentList = rentalFacade.getAvailableEquipment();

            for (Equipment equipment : equipmentList) {
                if (!equipment.getCategory().equals(selectedCategory)) {
                    continue;
                }

                String equipmentRecord = equipment.getEquipmentId()
                        + " - "
                        + equipment.getName()
                        + " - RM"
                        + equipment.getDailyRate()
                        + "/day";

                if (!searchText.isEmpty() && !equipmentRecord.toLowerCase().contains(searchText)) {
                    continue;
                }

                availableEquipmentComboBox.addItem(equipmentRecord);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading available equipment.");
            e.printStackTrace();
        }
    }

    private void loadActiveRentals() {
        try {
            loadingActiveRentals = true;
            activeRentalComboBox.removeAllItems();

            List<Rental> rentalList = rentalFacade.getActiveRentals();

            for (Rental rental : rentalList) {
                activeRentalComboBox.addItem(
                        rental.getRentalId()
                                + " - "
                                + rental.getEquipmentId()
                                + " - "
                                + rental.getUserName()
                );
            }

            loadingActiveRentals = false;
            updateReturnRentalDetails();

        } catch (SQLException e) {
            loadingActiveRentals = false;
            JOptionPane.showMessageDialog(this, "Database error while loading active rentals.");
            e.printStackTrace();
        }
    }

    private void loadRentalHistory() {
        try {
            rentalHistoryTableModel.setRowCount(0);

            List<Rental> rentalList = rentalFacade.getRentalHistory();

            for (Rental rental : rentalList) {
                Object[] row = {
                        rental.getRentalId(),
                        rental.getUserId() + " - " + rental.getUserName() + " (" + rental.getUserType() + ")",
                        rental.getEquipmentId() + " - " + rental.getEquipmentName(),
                        rental.getRentalDate(),
                        rental.getExpectedReturnDate(),
                        rental.getActualReturnDate() == null ? "-" : rental.getActualReturnDate(),
                        rental.getStatus(),
                        rental.getTotalFee(),
                        rental.getStatus() == RentalStatus.ACTIVE ? "Unavailable" : "Available"
                };

                rentalHistoryTableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading rental history.");
            e.printStackTrace();
        }
    }

    public void refreshRentalData() {
        loadAvailableEquipment();
        loadActiveRentals();
        loadRentalHistory();
    }

    private void updateReturnRentalDetails() {
        if (loadingActiveRentals || returnRentalDetailsArea == null) {
            return;
        }

        try {
            Rental rental = getSelectedActiveRental();

            if (rental == null) {
                returnRentalDetailsArea.setText("No active rental selected.");
                returnDamagedCheckBox.setSelected(false);
                return;
            }

            int lateDays = rentalFacade.calculateLateDays(rental.getRentalId());

            returnRentalDetailsArea.setText("""
                    Rental ID: %s
                    User Name: %s
                    User Type: %s
                    Equipment: %s
                    Rental Date: %s
                    Expected Return Date: %s
                    Actual Return Date: %s
                    Late Days: %d
                    """.formatted(
                    rental.getRentalId(),
                    rental.getUserName(),
                    rental.getUserType(),
                    rental.getEquipmentName(),
                    rental.getRentalDate(),
                    rental.getExpectedReturnDate(),
                    LocalDate.now(),
                    lateDays
            ));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading rental details.");
            e.printStackTrace();
        }
    }

    private Rental getSelectedActiveRental() throws SQLException {
        String rentalId = getSelectedId(activeRentalComboBox);

        if (rentalId.isBlank()) {
            return null;
        }

        List<Rental> rentalList = rentalFacade.getActiveRentals();

        for (Rental rental : rentalList) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    private String getSelectedId(JComboBox<String> comboBox) {
        Object selectedItem = comboBox.getSelectedItem();

        if (selectedItem == null) {
            return "";
        }

        return selectedItem.toString().split(" - ")[0];
    }

    private void clearRentFields() {
        userIdField.setText("");
        userNameField.setText("");
        userTypeComboBox.setSelectedIndex(0);
        finalYearCheckBox.setSelected(false);
        updateFinalYearCheckBox();
        categoryComboBox.setSelectedIndex(0);
        equipmentSearchField.setText("");
        durationField.setText("");
    }

    private String getSelectedUserType() {
        String userType = userTypeComboBox.getSelectedItem().toString();

        if ("Student".equals(userType) && finalYearCheckBox.isSelected()) {
            return "Final-Year Student";
        }

        return userType;
    }

    private void updateFinalYearCheckBox() {
        boolean studentSelected = "Student".equals(userTypeComboBox.getSelectedItem().toString());

        finalYearCheckBox.setEnabled(studentSelected);

        if (!studentSelected) {
            finalYearCheckBox.setSelected(false);
        }
    }
}
