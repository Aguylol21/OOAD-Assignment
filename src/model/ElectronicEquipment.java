package model;

public class ElectronicEquipment extends Equipment {

    public ElectronicEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, "Electronic Equipment", status);
    }

    @Override
    public String getEquipmentType() {
        return "Electronic Equipment";
    }
}