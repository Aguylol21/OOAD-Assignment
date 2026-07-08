package service;

import model.Equipment;
import model.EquipmentStatus;
import model.Rental;
import model.RentalStatus;
import repository.RentalRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RentalManager {

    private final RentalRepository rentalRepository;
    private final EquipmentService equipmentService;

    public RentalManager(
            RentalRepository rentalRepository,
            EquipmentService equipmentService
    ) {
        this.rentalRepository = rentalRepository;
        this.equipmentService = equipmentService;
    }

    public Rental rentEquipment(
            String userId,
            String userName,
            String userType,
            String equipmentId,
            int durationDays
    ) throws SQLException {
        validateRentalInput(userId, userName, userType, equipmentId, durationDays);

        Equipment equipment = equipmentService.getEquipmentById(equipmentId);

        if (!equipment.isAvailable()) {
            throw new IllegalArgumentException("Equipment is not available.");
        }

        if (rentalRepository.findActiveByEquipmentId(equipmentId).isPresent()) {
            throw new IllegalArgumentException("Equipment is already rented.");
        }

        LocalDate rentalDate = LocalDate.now();
        LocalDate expectedReturnDate = rentalDate.plusDays(durationDays);
        int totalFee = (int) Math.round(equipment.calculateRentalFee(durationDays));

        Rental rental = new Rental(
                generateRentalId(),
                userId,
                userName,
                userType,
                equipment.getEquipmentId(),
                equipment.getName(),
                rentalDate,
                expectedReturnDate,
                null,
                RentalStatus.ACTIVE,
                totalFee
        );

        rentalRepository.add(rental);
        equipmentService.updateEquipment(
                equipment.getEquipmentId(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getDailyRate(),
                EquipmentStatus.RENTED
        );

        return rental;
    }

    public Rental returnEquipment(String rentalId) throws SQLException {
        if (rentalId == null || rentalId.isBlank()) {
            throw new IllegalArgumentException("Please select a rental record.");
        }

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental record not found."));

        if (!rental.isActive()) {
            throw new IllegalArgumentException("Rental has already been returned.");
        }

        rental.markReturned(LocalDate.now());
        rentalRepository.update(rental);

        Equipment equipment = equipmentService.getEquipmentById(rental.getEquipmentId());
        equipmentService.updateEquipment(
                equipment.getEquipmentId(),
                equipment.getName(),
                equipment.getCategory(),
                equipment.getDailyRate(),
                EquipmentStatus.AVAILABLE
        );

        return rental;
    }

    public List<Rental> getRentalHistory() throws SQLException {
        return rentalRepository.findAll();
    }

    public Rental getRentalById(String rentalId) throws SQLException {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental record not found."));
    }

    public List<Rental> getActiveRentals() throws SQLException {
        return rentalRepository.findActive();
    }

    private String generateRentalId() throws SQLException {
        int maxRentalNumber = 0;

        for (Rental rental : rentalRepository.findAll()) {
            String rentalId = rental.getRentalId();

            if (rentalId != null && rentalId.startsWith("RNT")) {
                try {
                    int rentalNumber = Integer.parseInt(rentalId.substring(3));
                    maxRentalNumber = Math.max(maxRentalNumber, rentalNumber);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return "RNT" + String.format("%03d", maxRentalNumber + 1);
    }

    private void validateRentalInput(
            String userId,
            String userName,
            String userType,
            String equipmentId,
            int durationDays
    ) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("User name is required.");
        }

        if (userType == null || userType.isBlank()) {
            throw new IllegalArgumentException("User type is required.");
        }

        if (equipmentId == null || equipmentId.isBlank()) {
            throw new IllegalArgumentException("Please select available equipment.");
        }

        if (durationDays <= 0) {
            throw new IllegalArgumentException("Rental duration must be greater than zero.");
        }
    }
}
