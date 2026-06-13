package repository;

import model.Equipment;
import java.util.ArrayList;

public class EquipmentRepository {
    private ArrayList<Equipment> equipmentList;

    public EquipmentRepository() {
        equipmentList = new ArrayList<>();
    }

    public void addEquipment(Equipment equipment) {
        equipmentList.add(equipment);
    }

    public ArrayList<Equipment> getAllEquipment() {
        return equipmentList;
    }

    public Equipment findById(String equipmentId) {
        for (Equipment equipment : equipmentList) {
            if (equipment.getEquipmentId().equalsIgnoreCase(equipmentId)) {
                return equipment;
            }
        }
        return null;
    }

    public boolean removeEquipment(String equipmentId) {
        Equipment equipment = findById(equipmentId);

        if (equipment != null) {
            equipmentList.remove(equipment);
            return true;
        }

        return false;
    }
}