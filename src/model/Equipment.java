package model;

public abstract class Equipment {

    private String equipmentId;
    private String name;
    private int dailyRate;
    private String category;
    private EquipmentStatus status;

    public Equipment(String equipmentId, String name, int dailyRate, String category, EquipmentStatus status) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.dailyRate = dailyRate;
        this.category = category;
        this.status = status;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(int dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == EquipmentStatus.AVAILABLE;
    }

    public abstract String getEquipmentType();
}