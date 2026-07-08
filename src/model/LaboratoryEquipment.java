package model;

public class LaboratoryEquipment extends Equipment {

    public LaboratoryEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, EquipmentCategoryConstants.LABORATORY, status);
    }

    @Override
    public double calculateRentalFee(int rentalDays) {
        double baseFee = getDailyRate() * rentalDays;
        return baseFee + (baseFee * 0.15);
    }

    @Override
    public String getEquipmentType() {
        return EquipmentCategoryConstants.LABORATORY;
    }
}
