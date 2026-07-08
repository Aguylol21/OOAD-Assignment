package service;

import model.Equipment;

public interface PenaltyPolicy {

    double calculateLatePenalty(int lateDays);

    double calculateDamagePenalty(boolean damaged);

    default double calculateLatePenalty(Equipment equipment, int lateDays) {
        return calculateLatePenalty(lateDays);
    }

    default double calculateDamagePenalty(Equipment equipment, boolean damaged) {
        return calculateDamagePenalty(damaged);
    }
}
