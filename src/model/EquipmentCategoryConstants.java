package model;

public final class EquipmentCategoryConstants {

    public static final String ELECTRONIC = "Electronic Equipment";
    public static final String MEDIA = "Media Equipment";
    public static final String LABORATORY = "Laboratory Equipment";

    private EquipmentCategoryConstants() {
        // Prevent object creation
    }

    public static String[] getAllCategories() {
        return new String[]{
                ELECTRONIC,
                MEDIA,
                LABORATORY
        };
    }

    public static String getPrefix(String category) {
        return switch (category) {
            case ELECTRONIC -> "ELE";
            case MEDIA -> "MED";
            case LABORATORY -> "LAB";
            default -> throw new IllegalArgumentException("Invalid equipment category.");
        };
    }
}
