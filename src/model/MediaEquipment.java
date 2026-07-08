package model;

public class MediaEquipment extends Equipment {

    public MediaEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, EquipmentCategoryConstants.MEDIA, status);
    }

    @Override
    public double calculateRentalFee(int rentalDays) {
        double baseFee = getDailyRate() * rentalDays;

        if (rentalDays >= 5) {
            return baseFee * 0.95;
        }

        return baseFee;
    }

    @Override
    public String getEquipmentType() {
        return EquipmentCategoryConstants.MEDIA;
    }
}
