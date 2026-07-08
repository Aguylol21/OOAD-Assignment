package service;

import model.*;
import repository.EquipmentRepository;

import java.sql.SQLException;
import java.util.List;

public class EquipmentService {

    private final EquipmentRepository repository;
    private final EquipmentFactory equipmentFactory;

    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
        this.equipmentFactory = new EquipmentFactory();
    }

    public void addEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate,
            EquipmentStatus status
    ) throws SQLException {

        if (equipmentId == null || equipmentId.isBlank()) {
            equipmentId = generateEquipmentId(category);
        }

        validateInput(equipmentId, name, dailyRate);

        if (repository.findById(equipmentId).isPresent()) {
            throw new IllegalArgumentException("Equipment ID already exists.");
        }

        Equipment equipment = equipmentFactory.createEquipment(
                equipmentId,
                name,
                category,
                dailyRate,
                status
        );

        repository.add(equipment);
    }

    public List<Equipment> getAllEquipment() throws SQLException {
        return repository.findAll();
    }

    public List<Equipment> getAvailableEquipment() throws SQLException {
        return repository.findAvailable();
    }

    public String generateEquipmentId(String category) throws SQLException {
        String prefix = EquipmentCategoryConstants.getPrefix(category);
        int maxEquipmentNumber = 0;

        for (Equipment equipment : repository.findAll()) {
            String equipmentId = equipment.getEquipmentId();

            if (equipmentId != null && equipmentId.startsWith(prefix)) {
                try {
                    int equipmentNumber = Integer.parseInt(equipmentId.substring(prefix.length()));
                    maxEquipmentNumber = Math.max(maxEquipmentNumber, equipmentNumber);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return prefix + String.format("%03d", maxEquipmentNumber + 1);
    }

    public Equipment getEquipmentById(String equipmentId) throws SQLException {
        return repository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found."));
    }

    public void updateEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate,
            EquipmentStatus status
    ) throws SQLException {

        Equipment existingEquipment = repository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found."));

        String newEquipmentId = equipmentId;

        if (!existingEquipment.getCategory().equals(category)) {
            newEquipmentId = generateEquipmentId(category);
        }

        validateInput(newEquipmentId, name, dailyRate);

        if (!newEquipmentId.equals(equipmentId) && repository.findById(newEquipmentId).isPresent()) {
            throw new IllegalArgumentException("Equipment ID already exists.");
        }

        Equipment equipment = equipmentFactory.createEquipment(
                newEquipmentId,
                name,
                category,
                dailyRate,
                status
        );

        repository.update(equipmentId, equipment);
    }

    public boolean removeEquipment(String equipmentId) throws SQLException {
        return repository.deleteById(equipmentId);
    }

    private void validateInput(
            String equipmentId,
            String name,
            int dailyRate
    ) {
        if (equipmentId == null || equipmentId.isBlank()) {
            throw new IllegalArgumentException("Equipment ID is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Equipment name is required.");
        }

        if (dailyRate <= 0) {
            throw new IllegalArgumentException("Daily rate must be greater than zero.");
        }
    }
}
