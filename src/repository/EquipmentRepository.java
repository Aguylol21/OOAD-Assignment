package repository;

import model.Equipment;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {

    void add(Equipment equipment) throws SQLException;

    void update(Equipment equipment) throws SQLException;

    boolean deleteById(String equipmentId) throws SQLException;

    Optional<Equipment> findById(String equipmentId) throws SQLException;

    List<Equipment> findAll() throws SQLException;

    List<Equipment> findAvailable() throws SQLException;
}