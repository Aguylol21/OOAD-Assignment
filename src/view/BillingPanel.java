package view;

import facade.RentalFacade;
import model.Bill;
import model.Rental;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillingPanel extends JPanel {

    private RentalFacade rentalFacade;

    private JComboBox<String> rentalComboBox;
    private JTextField lateDaysField;
    private JCheckBox damagedCheckBox;
    private JTextField invoiceSearchField;
    private JComboBox<String> invoiceRentalComboBox;
    private JTextField invoiceLateDaysField;
    private JCheckBox invoiceDamagedCheckBox;
    private JTextArea billTextArea;
    private JTextArea invoiceTextArea;
    private List<String> invoiceRentalRecords = new ArrayList<>();
    private boolean loadingInvoiceRecords;

    public BillingPanel(RentalFacade rentalFacade) {
        this.rentalFacade = rentalFacade;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Bill Display", createBillDisplayPanel());
        tabbedPane.addTab("Payment Summary / Invoice", createInvoicePanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadRentalRecords();
    }

    private JPanel createBillDisplayPanel() {
        JPanel billPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Bill"));

        rentalComboBox = new JComboBox<>();
        lateDaysField = new JTextField("0");
        lateDaysField.setEditable(false);
        damagedCheckBox = new JCheckBox("Damaged");

        formPanel.add(new JLabel("Rental Record:"));
        formPanel.add(rentalComboBox);

        formPanel.add(new JLabel("Late Days:"));
        formPanel.add(lateDaysField);

        formPanel.add(new JLabel("Damaged Equipment:"));
        formPanel.add(damagedCheckBox);

        JButton generateButton = new JButton("Generate Bill");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateButton);
        buttonPanel.add(refreshButton);

        billTextArea = new JTextArea();
        billTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(billTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bill Details"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        billPanel.add(topPanel, BorderLayout.NORTH);
        billPanel.add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateBill());
        refreshButton.addActionListener(e -> refreshBillingData());
        rentalComboBox.addActionListener(e -> updateLateDaysField());

        return billPanel;
    }

    private JPanel createInvoicePanel() {
        JPanel invoicePanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Generate Invoice"));

        invoiceSearchField = new JTextField();
        invoiceRentalComboBox = new JComboBox<>();
        invoiceLateDaysField = new JTextField("0");
        invoiceLateDaysField.setEditable(false);
        invoiceDamagedCheckBox = new JCheckBox("Damaged");

        formPanel.add(new JLabel("Search:"));
        formPanel.add(invoiceSearchField);

        formPanel.add(new JLabel("Rental Record:"));
        formPanel.add(invoiceRentalComboBox);

        formPanel.add(new JLabel("Late Days:"));
        formPanel.add(invoiceLateDaysField);

        formPanel.add(new JLabel("Damaged Equipment:"));
        formPanel.add(invoiceDamagedCheckBox);

        invoiceTextArea = new JTextArea();
        invoiceTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(invoiceTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payment Summary / Invoice"));

        JButton generateButton = new JButton("Generate Invoice");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateButton);
        buttonPanel.add(refreshButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        invoicePanel.add(topPanel, BorderLayout.NORTH);
        invoicePanel.add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateInvoice());
        refreshButton.addActionListener(e -> refreshBillingData());
        invoiceRentalComboBox.addActionListener(e -> updateInvoiceLateDaysField());
        invoiceSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterInvoiceRentalRecords();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterInvoiceRentalRecords();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterInvoiceRentalRecords();
            }
        });

        return invoicePanel;
    }

    private void generateBill() {
        try {
            String rentalId = getSelectedId(rentalComboBox);
            boolean damaged = damagedCheckBox.isSelected();

            Bill bill = rentalFacade.calculateBill(
                    rentalId,
                    damaged
            );

            lateDaysField.setText(String.valueOf(bill.getLateDays()));
            billTextArea.setText(bill.generateBillText());
            invoiceTextArea.setText(bill.generateInvoiceText());

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while generating bill.");
            e.printStackTrace();
        }
    }

    private void generateInvoice() {
        try {
            String rentalId = getSelectedId(invoiceRentalComboBox);
            boolean damaged = invoiceDamagedCheckBox.isSelected();

            Bill bill = rentalFacade.calculateBill(
                    rentalId,
                    damaged
            );

            invoiceLateDaysField.setText(String.valueOf(bill.getLateDays()));
            invoiceTextArea.setText(bill.generateInvoiceText());
            billTextArea.setText(bill.generateBillText());

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while generating invoice.");
            e.printStackTrace();
        }
    }

    private void loadRentalRecords() {
        try {
            rentalComboBox.removeAllItems();
            invoiceRentalComboBox.removeAllItems();
            invoiceRentalRecords.clear();

            List<Rental> activeRentalList = rentalFacade.getActiveRentals();
            List<Rental> rentalHistoryList = rentalFacade.getRentalHistory();

            for (Rental rental : activeRentalList) {
                String rentalRecord = rental.getRentalId()
                        + " - "
                        + rental.getEquipmentId()
                        + " - "
                        + rental.getUserName();

                rentalComboBox.addItem(rentalRecord);
            }

            for (Rental rental : rentalHistoryList) {
                String rentalRecord = rental.getRentalId()
                        + " - "
                        + rental.getEquipmentId()
                        + " - "
                        + rental.getUserName();

                invoiceRentalRecords.add(rentalRecord);
            }

            filterInvoiceRentalRecords();

            updateLateDaysField();
            updateInvoiceLateDaysField();
            billTextArea.setText("");
            invoiceTextArea.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading rental records.");
            e.printStackTrace();
        }
    }

    public void refreshBillingData() {
        loadRentalRecords();
    }

    private void updateLateDaysField() {
        try {
            String rentalId = getSelectedId(rentalComboBox);
            int lateDays = rentalFacade.calculateLateDays(rentalId);

            lateDaysField.setText(String.valueOf(lateDays));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while calculating late days.");
            e.printStackTrace();
        }
    }

    private void updateInvoiceLateDaysField() {
        if (loadingInvoiceRecords) {
            return;
        }

        try {
            String rentalId = getSelectedId(invoiceRentalComboBox);
            int lateDays = rentalFacade.calculateLateDays(rentalId);

            invoiceLateDaysField.setText(String.valueOf(lateDays));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while calculating late days.");
            e.printStackTrace();
        }
    }

    private void filterInvoiceRentalRecords() {
        loadingInvoiceRecords = true;
        invoiceRentalComboBox.removeAllItems();

        String searchText = invoiceSearchField.getText().trim().toLowerCase();

        for (String rentalRecord : invoiceRentalRecords) {
            if (searchText.isEmpty() || rentalRecord.toLowerCase().contains(searchText)) {
                invoiceRentalComboBox.addItem(rentalRecord);
            }
        }

        loadingInvoiceRecords = false;
        updateInvoiceLateDaysField();
    }

    private String getSelectedId(JComboBox<String> comboBox) {
        Object selectedItem = comboBox.getSelectedItem();

        if (selectedItem == null) {
            return "";
        }

        return selectedItem.toString().split(" - ")[0];
    }
}
