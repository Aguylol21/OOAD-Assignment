package service;

import model.Equipment;
import model.EquipmentCategoryConstants;

public class StandardPenaltyPolicy implements PenaltyPolicy {

    @Override
    public double calculateLatePenalty(int lateDays) {
        return lateDays * 10;
    }

    @Override
    public double calculateDamagePenalty(boolean damaged) {
        if (damaged) {
            return 50;
        }

        return 0;
    }

    @Override
    public double calculateLatePenalty(Equipment equipment, int lateDays) {
        return switch (equipment.getCategory()) {
            case EquipmentCategoryConstants.ELECTRONIC -> lateDays * 12;
            case EquipmentCategoryConstants.MEDIA -> lateDays * 8;
            case EquipmentCategoryConstants.LABORATORY -> lateDays * 15;
            default -> calculateLatePenalty(lateDays);
        };
    }

    @Override
    public double calculateDamagePenalty(Equipment equipment, boolean damaged) {
        if (!damaged) {
            return 0;
        }

        return switch (equipment.getCategory()) {
            case EquipmentCategoryConstants.ELECTRONIC -> 50;
            case EquipmentCategoryConstants.MEDIA -> 20;
            case EquipmentCategoryConstants.LABORATORY -> 30;
            default -> calculateDamagePenalty(true);
        };
    }
}
