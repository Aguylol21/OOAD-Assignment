package model;

public class LaboratoryEquipment extends Equipment {

    public LaboratoryEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, "Laboratory Equipment", status);
    }

    @Override
    public String getEquipmentType() {
        return "Laboratory Equipment";
    }
}