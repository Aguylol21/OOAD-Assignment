package repository;

import model.Equipment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEquipmentRepository implements EquipmentRepository {

    private final Map<String, Equipment> equipmentMap = new LinkedHashMap<>();

    @Override
    public void add(Equipment equipment) throws SQLException {
        equipmentMap.put(equipment.getEquipmentId(), equipment);
    }

    @Override
    public void update(Equipment equipment) throws SQLException {
        equipmentMap.put(equipment.getEquipmentId(), equipment);
    }

    @Override
    public void update(String oldEquipmentId, Equipment equipment) throws SQLException {
        equipmentMap.remove(oldEquipmentId);
        equipmentMap.put(equipment.getEquipmentId(), equipment);
    }

    @Override
    public boolean deleteById(String equipmentId) throws SQLException {
        return equipmentMap.remove(equipmentId) != null;
    }

    @Override
    public Optional<Equipment> findById(String equipmentId) throws SQLException {
        return Optional.ofNullable(equipmentMap.get(equipmentId));
    }

    @Override
    public List<Equipment> findAll() throws SQLException {
        return new ArrayList<>(equipmentMap.values());
    }

    @Override
    public List<Equipment> findAvailable() throws SQLException {
        List<Equipment> availableEquipment = new ArrayList<>();

        for (Equipment equipment : equipmentMap.values()) {
            if (equipment.isAvailable()) {
                availableEquipment.add(equipment);
            }
        }

        return availableEquipment;
    }
}
