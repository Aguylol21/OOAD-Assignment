package model;

public abstract class Equipment {
    private int equipmentId;
    private String name;
    private int dailyRate;
    private String category;
    private Boolean isAvailable;
    EquipmentStatus status;

    public Equipment(int equipmentId, String name, int dailyRate, String category, Boolean isAvailable) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.dailyRate = dailyRate;
        this.category = category;
        this.isAvailable = true;
        this.status = status;
    }

    public int getEquipmentId() {
        return equipmentId;
    }
    public void setEquipmentId(int equipmentId) {
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public abstract String getEquipmentType();

    public EquipmentStatus getStatus() {
        return status;
    }
}
