package facade;

import model.Equipment;
import model.EquipmentStatus;
import model.Bill;
import model.Rental;
import service.BillingService;
import service.EquipmentService;
import service.RentalManager;

import java.sql.SQLException;
import java.util.List;

public class RentalFacade {

    private final EquipmentService equipmentService;
    private final RentalManager rentalManager;
    private final BillingService billingService;

    public RentalFacade(EquipmentService equipmentService) {
        this(equipmentService, null, null);
    }

    public RentalFacade(
            EquipmentService equipmentService,
            RentalManager rentalManager
    ) {
        this(equipmentService, rentalManager, null);
    }

    public RentalFacade(
            EquipmentService equipmentService,
            RentalManager rentalManager,
            BillingService billingService
    ) {
        this.equipmentService = equipmentService;
        this.rentalManager = rentalManager;
        this.billingService = billingService;
    }

    public void addEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate,
            EquipmentStatus status
    ) throws SQLException {
        equipmentService.addEquipment(
                equipmentId,
                name,
                category,
                dailyRate,
                status
        );
    }

    public List<Equipment> getAllEquipment() throws SQLException {
        return equipmentService.getAllEquipment();
    }

    public List<Equipment> getAvailableEquipment() throws SQLException {
        return equipmentService.getAvailableEquipment();
    }

    public String generateEquipmentId(String category) throws SQLException {
        return equipmentService.generateEquipmentId(category);
    }

    public void updateEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate,
            EquipmentStatus status
    ) throws SQLException {
        equipmentService.updateEquipment(
                equipmentId,
                name,
                category,
                dailyRate,
                status
        );
    }

    public boolean removeEquipment(String equipmentId) throws SQLException {
        return equipmentService.removeEquipment(equipmentId);
    }

    public Rental rentEquipment(
            String userId,
            String userName,
            String userType,
            String equipmentId,
            int durationDays
    ) throws SQLException {
        return rentalManager.rentEquipment(
                userId,
                userName,
                userType,
                equipmentId,
                durationDays
        );
    }

    public Rental returnEquipment(String rentalId) throws SQLException {
        return rentalManager.returnEquipment(rentalId);
    }

    public List<Rental> getRentalHistory() throws SQLException {
        return rentalManager.getRentalHistory();
    }

    public List<Rental> getActiveRentals() throws SQLException {
        return rentalManager.getActiveRentals();
    }

    public Bill calculateBill(
            String rentalId,
            int lateDays,
            boolean damaged
    ) throws SQLException {
        return billingService.calculateBill(
                rentalId,
                lateDays,
                damaged
        );
    }

    public Bill calculateBill(
            String rentalId,
            boolean damaged
    ) throws SQLException {
        return billingService.calculateBill(
                rentalId,
                damaged
        );
    }

    public int calculateLateDays(String rentalId) throws SQLException {
        return billingService.calculateLateDays(rentalId);
    }
}
