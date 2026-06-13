package model;

public class MediaEquipment extends Equipment {
    public MediaEquipment(int equipmentId, String name, int dailyRate, String category) {
        super(equipmentId, name, dailyRate, category, false);
    }

    @Override
    public String getEquipmentType() {
        return "Media equipment";
    }
    
}
