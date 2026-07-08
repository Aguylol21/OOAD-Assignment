package repository;

import model.Rental;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RentalRepository {

    void add(Rental rental) throws SQLException;

    void update(Rental rental) throws SQLException;

    Optional<Rental> findById(String rentalId) throws SQLException;

    Optional<Rental> findActiveByEquipmentId(String equipmentId) throws SQLException;

    List<Rental> findAll() throws SQLException;

    List<Rental> findActive() throws SQLException;
}
