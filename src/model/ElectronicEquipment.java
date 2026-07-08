package model;

public class ElectronicEquipment extends Equipment {

    public ElectronicEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, EquipmentCategoryConstants.ELECTRONIC, status);
    }

    @Override
    public double calculateRentalFee(int rentalDays) {
        double baseFee = getDailyRate() * rentalDays;
        return baseFee + (baseFee * 0.10);
    }

    @Override
    public String getEquipmentType() {
        return EquipmentCategoryConstants.ELECTRONIC;
    }
}
