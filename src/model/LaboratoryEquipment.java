package model;

public class LaboratoryEquipment extends Equipment {
    public LaboratoryEquipment(int equipmentId, String name, int dailyRate, String category) {
        super(equipmentId, name, dailyRate, category, false);
    }

    @Override
    public String getEquipmentType() {
        return "Laboratory equipment";
    }
    
}
