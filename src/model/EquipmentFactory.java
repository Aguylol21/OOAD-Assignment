package model;

public class EquipmentFactory {

    public Equipment createEquipment(
            String equipmentId,
            String name,
            String category,
            int dailyRate,
            EquipmentStatus status
    ) {
        return switch (category) {
            case EquipmentCategoryConstants.ELECTRONIC, "ELECTRONIC" -> new ElectronicEquipment(
                    equipmentId,
                    name,
                    dailyRate,
                    status
            );

            case EquipmentCategoryConstants.MEDIA, "MEDIA" -> new MediaEquipment(
                    equipmentId,
                    name,
                    dailyRate,
                    status
            );

            case EquipmentCategoryConstants.LABORATORY, "LABORATORY" -> new LaboratoryEquipment(
                    equipmentId,
                    name,
                    dailyRate,
                    status
            );

            default -> throw new IllegalArgumentException("Invalid equipment category.");
        };
    }
}
