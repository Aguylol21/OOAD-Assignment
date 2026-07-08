package model;

public class Staff extends User {

    public Staff(String userId, String name) {
        super(userId, name);
    }

    @Override
    public String getUserType() {
        return "Staff";
    }

    @Override
    public double getDiscountRate() {
        return 0.10;
    }
}
