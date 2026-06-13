package view;

import model.Equipment;
import model.EquipmentStatus;
import service.EquipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EquipmentPanel extends JPanel {

    private EquipmentService equipmentService;

    private JTextField idField;
    private JTextField nameField;
    private JTextField rateField;

    private JComboBox<String> categoryComboBox;
    private JComboBox<EquipmentStatus> statusComboBox;

    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    public EquipmentPanel(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;

        setLayout(new BorderLayout());

        createFormPanel();
        createTablePanel();
        loadEquipmentTable();
    }

    private void createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Equipment Management"));

        idField = new JTextField();
        nameField = new JTextField();
        rateField = new JTextField();

        categoryComboBox = new JComboBox<>(new String[]{
                "Electronic Equipment",
                "Media Equipment",
                "Laboratory Equipment"
        });

        statusComboBox = new JComboBox<>(EquipmentStatus.values());

        JButton addButton = new JButton("Add Equipment");
        JButton updateButton = new JButton("Update Equipment");
        JButton deleteButton = new JButton("Delete Equipment");
        JButton clearButton = new JButton("Clear");

        formPanel.add(new JLabel("Equipment ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Equipment Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);

        formPanel.add(new JLabel("Daily Rate (RM):"));
        formPanel.add(rateField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> addEquipment());
        updateButton.addActionListener(e -> updateEquipment());
        deleteButton.addActionListener(e -> deleteEquipment());
        clearButton.addActionListener(e -> clearFields());
    }

    private void createTablePanel() {
        String[] columns = {
                "Equipment ID",
                "Name",
                "Category",
                "Daily Rate",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0);
        equipmentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Equipment List"));

        add(scrollPane, BorderLayout.CENTER);

        equipmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFieldsFromSelectedRow();
            }
        });
    }

    private void addEquipment() {
        try {
            String equipmentId = idField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryComboBox.getSelectedItem().toString();
            int dailyRate = Integer.parseInt(rateField.getText().trim());

            equipmentService.addEquipment(
                    equipmentId,
                    name,
                    category,
                    dailyRate
            );

            JOptionPane.showMessageDialog(this, "Equipment added successfully.");
            clearFields();
            loadEquipmentTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a valid number.");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while adding equipment.");
            e.printStackTrace();
        }
    }

    private void updateEquipment() {
        try {
            String equipmentId = idField.getText().trim();
            String name = nameField.getText().trim();
            int dailyRate = Integer.parseInt(rateField.getText().trim());
            EquipmentStatus status = (EquipmentStatus) statusComboBox.getSelectedItem();

            equipmentService.updateEquipment(
                    equipmentId,
                    name,
                    dailyRate,
                    status
            );

            JOptionPane.showMessageDialog(this, "Equipment updated successfully.");
            clearFields();
            loadEquipmentTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a valid number.");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while updating equipment.");
            e.printStackTrace();
        }
    }

    private void deleteEquipment() {
        try {
            String equipmentId = idField.getText().trim();

            if (equipmentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter or select an equipment ID.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this equipment?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = equipmentService.removeEquipment(equipmentId);

                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Equipment deleted successfully.");
                    clearFields();
                    loadEquipmentTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Equipment not found.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while deleting equipment.");
            e.printStackTrace();
        }
    }

    private void loadEquipmentTable() {
        try {
            tableModel.setRowCount(0);

            List<Equipment> equipmentList = equipmentService.getAllEquipment();

            for (Equipment equipment : equipmentList) {
                Object[] row = {
                        equipment.getEquipmentId(),
                        equipment.getName(),
                        equipment.getCategory(),
                        equipment.getDailyRate(),
                        equipment.getStatus()
                };

                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error while loading equipment.");
            e.printStackTrace();
        }
    }

    private void fillFieldsFromSelectedRow() {
        int selectedRow = equipmentTable.getSelectedRow();

        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            categoryComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
            rateField.setText(tableModel.getValueAt(selectedRow, 3).toString());

            EquipmentStatus status = EquipmentStatus.valueOf(
                    tableModel.getValueAt(selectedRow, 4).toString()
            );

            statusComboBox.setSelectedItem(status);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        rateField.setText("");
        categoryComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedItem(EquipmentStatus.AVAILABLE);
        equipmentTable.clearSelection();
    }
}