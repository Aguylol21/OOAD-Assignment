package repository;

import database.DatabaseManager;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteEquipmentRepository implements EquipmentRepository {

    private final EquipmentFactory equipmentFactory = new EquipmentFactory();

    @Override
    public void add(Equipment equipment) throws SQLException {
        String sql = """
            INSERT INTO equipment 
            (equipment_id, name, daily_rate, category, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, equipment.getEquipmentId());
            statement.setString(2, equipment.getName());
            statement.setInt(3, equipment.getDailyRate());
            statement.setString(4, equipment.getCategory());
            statement.setString(5, equipment.getStatus().name());

            statement.executeUpdate();
        }
    }

    @Override
    public void update(Equipment equipment) throws SQLException {
        update(equipment.getEquipmentId(), equipment);
    }

    @Override
    public void update(String oldEquipmentId, Equipment equipment) throws SQLException {
        String sql = """
            UPDATE equipment
            SET equipment_id = ?, name = ?, daily_rate = ?, category = ?, status = ?
            WHERE equipment_id = ?
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, equipment.getEquipmentId());
            statement.setString(2, equipment.getName());
            statement.setInt(3, equipment.getDailyRate());
            statement.setString(4, equipment.getCategory());
            statement.setString(5, equipment.getStatus().name());
            statement.setString(6, oldEquipmentId);

            statement.executeUpdate();
        }

        updateRentalEquipmentId(oldEquipmentId, equipment.getEquipmentId());
    }

    @Override
    public boolean deleteById(String equipmentId) throws SQLException {
        String sql = "DELETE FROM equipment WHERE equipment_id = ?";

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, equipmentId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    @Override
    public Optional<Equipment> findById(String equipmentId) throws SQLException {
        String sql = "SELECT * FROM equipment WHERE equipment_id = ?";

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, equipmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToEquipment(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Equipment> findAll() throws SQLException {
        String sql = "SELECT * FROM equipment ORDER BY equipment_id";

        List<Equipment> equipmentList = new ArrayList<>();

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                equipmentList.add(mapRowToEquipment(resultSet));
            }
        }

        return equipmentList;
    }

    @Override
    public List<Equipment> findAvailable() throws SQLException {
        String sql = """
            SELECT * FROM equipment
            WHERE status = 'AVAILABLE'
            ORDER BY equipment_id
            """;

        List<Equipment> equipmentList = new ArrayList<>();

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                equipmentList.add(mapRowToEquipment(resultSet));
            }
        }

        return equipmentList;
    }

    private Equipment mapRowToEquipment(ResultSet resultSet) throws SQLException {
        String equipmentId = resultSet.getString("equipment_id");
        String name = resultSet.getString("name");
        int dailyRate = resultSet.getInt("daily_rate");
        String category = resultSet.getString("category");

        EquipmentStatus status = mapStatus(resultSet.getString("status"));

        try {
            return equipmentFactory.createEquipment(
                    equipmentId,
                    name,
                    category,
                    dailyRate,
                    status
            );
        } catch (IllegalArgumentException e) {
            throw new SQLException(
                    "Unknown equipment category: " + category,
                    e
            );
        }
    }

    private EquipmentStatus mapStatus(String status) {
        try {
            return EquipmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return EquipmentStatus.UNDER_MAINTENANCE;
        }
    }

    private void updateRentalEquipmentId(String oldEquipmentId, String newEquipmentId) throws SQLException {
        if (oldEquipmentId.equals(newEquipmentId)) {
            return;
        }

        String sql = """
            UPDATE rental
            SET equipment_id = ?
            WHERE equipment_id = ?
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, newEquipmentId);
            statement.setString(2, oldEquipmentId);

            statement.executeUpdate();
        }
    }
}
