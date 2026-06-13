package model;

public class ElectronicEquipment extends Equipment {
    public ElectronicEquipment(int equipmentId, String name, int dailyRate, String category) {
        super(equipmentId, name, dailyRate, category, true);
    }

    @Override
    public String getEquipmentType() {
        return "Electronic equipment";
    }
}
