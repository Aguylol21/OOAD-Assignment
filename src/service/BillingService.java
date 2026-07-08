package service;

import model.Bill;
import model.Equipment;
import model.Rental;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BillingService {

    private final RentalManager rentalManager;
    private final EquipmentService equipmentService;
    private final DiscountPolicy discountPolicy;
    private final PenaltyPolicy penaltyPolicy;

    public BillingService(
            RentalManager rentalManager,
            EquipmentService equipmentService,
            DiscountPolicy discountPolicy,
            PenaltyPolicy penaltyPolicy
    ) {
        this.rentalManager = rentalManager;
        this.equipmentService = equipmentService;
        this.discountPolicy = discountPolicy;
        this.penaltyPolicy = penaltyPolicy;
    }

    public Bill calculateBill(
            String rentalId,
            int lateDays,
            boolean damaged
    ) throws SQLException {
        if (rentalId == null || rentalId.isBlank()) {
            throw new IllegalArgumentException("Please select a rental record.");
        }

        if (lateDays < 0) {
            throw new IllegalArgumentException("Late days cannot be negative.");
        }

        Rental rental = rentalManager.getRentalById(rentalId);
        Equipment equipment = equipmentService.getEquipmentById(rental.getEquipmentId());

        if (equipment.getDailyRate() <= 0) {
            throw new IllegalArgumentException("Equipment daily rate is invalid.");
        }

        int rentalDuration = (int) ChronoUnit.DAYS.between(
                rental.getRentalDate(),
                rental.getExpectedReturnDate()
        );

        if (rentalDuration <= 0) {
            throw new IllegalArgumentException("Rental duration is invalid.");
        }

        double baseFee = equipment.calculateRentalFee(rentalDuration);
        double discountAmount = discountPolicy.calculateDiscount(rental, baseFee);
        double discountRate = discountPolicy.getDiscountRate(rental);
        double latePenalty = penaltyPolicy.calculateLatePenalty(equipment, lateDays);
        double damagePenalty = penaltyPolicy.calculateDamagePenalty(equipment, damaged);

        return new Bill(
                rental,
                equipment,
                rentalDuration,
                baseFee,
                discountPolicy.getDiscountType(rental),
                discountRate,
                discountAmount,
                lateDays,
                latePenalty,
                damaged,
                damagePenalty
        );
    }

    public Bill calculateBill(
            String rentalId,
            boolean damaged
    ) throws SQLException {
        return calculateBill(
                rentalId,
                calculateLateDays(rentalId),
                damaged
        );
    }

    public int calculateLateDays(String rentalId) throws SQLException {
        if (rentalId == null || rentalId.isBlank()) {
            return 0;
        }

        Rental rental = rentalManager.getRentalById(rentalId);
        LocalDate returnDate = rental.getActualReturnDate();

        if (returnDate == null) {
            returnDate = LocalDate.now();
        }

        long lateDays = ChronoUnit.DAYS.between(
                rental.getExpectedReturnDate(),
                returnDate
        );

        if (lateDays <= 0) {
            return 0;
        }

        return (int) lateDays;
    }
}
