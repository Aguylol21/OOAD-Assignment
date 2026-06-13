package service;

import model.*;
import repository.EquipmentRepository;

import java.sql.SQLException;
import java.util.List;

public class EquipmentService {

    private final EquipmentRepository repository;

    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
    }

    public void addEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate
    ) throws SQLException {

        validateInput(equipmentId, name, dailyRate);

        if (repository.findById(equipmentId).isPresent()) {
            throw new IllegalArgumentException("Equipment ID already exists.");
        }

        Equipment equipment = createEquipment(
                equipmentId,
                name,
                category,
                dailyRate
        );

        repository.add(equipment);
    }

    public List<Equipment> getAllEquipment() throws SQLException {
        return repository.findAll();
    }

    public List<Equipment> getAvailableEquipment() throws SQLException {
        return repository.findAvailable();
    }

    public void updateEquipment(
            String equipmentId,
            String name,
            int dailyRate,
            EquipmentStatus status
    ) throws SQLException {

        Equipment equipment = repository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found."));

        validateInput(equipmentId, name, dailyRate);

        equipment.setName(name);
        equipment.setDailyRate(dailyRate);
        equipment.setStatus(status);

        repository.update(equipment);
    }

    public boolean removeEquipment(String equipmentId) throws SQLException {
        return repository.deleteById(equipmentId);
    }

    private Equipment createEquipment(
            String id,
            String name,
            String category,
            int rate
    ) {
        return switch (category) {
            case "Electronic Equipment" -> new ElectronicEquipment(
                    id,
                    name,
                    rate,
                    EquipmentStatus.AVAILABLE
            );

            case "Media Equipment" -> new MediaEquipment(
                    id,
                    name,
                    rate,
                    EquipmentStatus.AVAILABLE
            );

            case "Laboratory Equipment" -> new LaboratoryEquipment(
                    id,
                    name,
                    rate,
                    EquipmentStatus.AVAILABLE
            );

            default -> throw new IllegalArgumentException("Invalid equipment category.");
        };
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