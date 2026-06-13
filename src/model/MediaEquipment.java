package model;

public class MediaEquipment extends Equipment {

    public MediaEquipment(String equipmentId, String name, int dailyRate, EquipmentStatus status) {
        super(equipmentId, name, dailyRate, "Media Equipment", status);
    }

    @Override
    public String getEquipmentType() {
        return "Media Equipment";
    }
}