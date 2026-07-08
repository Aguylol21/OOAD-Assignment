package repository;

import database.DatabaseManager;
import model.Rental;
import model.RentalStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteRentalRepository implements RentalRepository {

    @Override
    public void add(Rental rental) throws SQLException {
        String sql = """
            INSERT INTO rental
            (rental_id, user_id, user_name, user_type, equipment_id, equipment_name,
             rental_date, expected_return_date, actual_return_date, status, total_fee)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setRentalValues(statement, rental);
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Rental rental) throws SQLException {
        String sql = """
            UPDATE rental
            SET user_id = ?, user_name = ?, user_type = ?, equipment_id = ?, equipment_name = ?,
                rental_date = ?, expected_return_date = ?, actual_return_date = ?, status = ?, total_fee = ?
            WHERE rental_id = ?
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, rental.getUserId());
            statement.setString(2, rental.getUserName());
            statement.setString(3, rental.getUserType());
            statement.setString(4, rental.getEquipmentId());
            statement.setString(5, rental.getEquipmentName());
            statement.setString(6, rental.getRentalDate().toString());
            statement.setString(7, rental.getExpectedReturnDate().toString());
            statement.setString(8, rental.getActualReturnDate() == null ? null : rental.getActualReturnDate().toString());
            statement.setString(9, rental.getStatus().name());
            statement.setInt(10, rental.getTotalFee());
            statement.setString(11, rental.getRentalId());

            statement.executeUpdate();
        }
    }

    @Override
    public Optional<Rental> findById(String rentalId) throws SQLException {
        String sql = "SELECT * FROM rental WHERE rental_id = ?";

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, rentalId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToRental(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Rental> findActiveByEquipmentId(String equipmentId) throws SQLException {
        String sql = """
            SELECT * FROM rental
            WHERE equipment_id = ? AND status = 'ACTIVE'
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, equipmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToRental(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Rental> findAll() throws SQLException {
        String sql = "SELECT * FROM rental ORDER BY rental_id";

        List<Rental> rentalList = new ArrayList<>();

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                rentalList.add(mapRowToRental(resultSet));
            }
        }

        return rentalList;
    }

    @Override
    public List<Rental> findActive() throws SQLException {
        String sql = """
            SELECT * FROM rental
            WHERE status = 'ACTIVE'
            ORDER BY rental_id
            """;

        List<Rental> rentalList = new ArrayList<>();

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                rentalList.add(mapRowToRental(resultSet));
            }
        }

        return rentalList;
    }

    private void setRentalValues(PreparedStatement statement, Rental rental) throws SQLException {
        statement.setString(1, rental.getRentalId());
        statement.setString(2, rental.getUserId());
        statement.setString(3, rental.getUserName());
        statement.setString(4, rental.getUserType());
        statement.setString(5, rental.getEquipmentId());
        statement.setString(6, rental.getEquipmentName());
        statement.setString(7, rental.getRentalDate().toString());
        statement.setString(8, rental.getExpectedReturnDate().toString());
        statement.setString(9, rental.getActualReturnDate() == null ? null : rental.getActualReturnDate().toString());
        statement.setString(10, rental.getStatus().name());
        statement.setInt(11, rental.getTotalFee());
    }

    private Rental mapRowToRental(ResultSet resultSet) throws SQLException {
        String actualReturnDate = resultSet.getString("actual_return_date");

        return new Rental(
                resultSet.getString("rental_id"),
                resultSet.getString("user_id"),
                resultSet.getString("user_name"),
                resultSet.getString("user_type"),
                resultSet.getString("equipment_id"),
                resultSet.getString("equipment_name"),
                LocalDate.parse(resultSet.getString("rental_date")),
                LocalDate.parse(resultSet.getString("expected_return_date")),
                actualReturnDate == null ? null : LocalDate.parse(actualReturnDate),
                RentalStatus.valueOf(resultSet.getString("status")),
                resultSet.getInt("total_fee")
        );
    }
}
