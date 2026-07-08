package model;

public class Student extends User {

    private boolean finalYear;

    public Student(String userId, String name, boolean finalYear) {
        super(userId, name);
        this.finalYear = finalYear;
    }

    public boolean isFinalYear() {
        return finalYear;
    }

    public void setFinalYear(boolean finalYear) {
        this.finalYear = finalYear;
    }

    @Override
    public String getUserType() {
        if (finalYear) {
            return "Final-Year Student";
        }

        return "Student";
    }

    @Override
    public double getDiscountRate() {
        if (finalYear) {
            return 0.05;
        }

        return 0;
    }
}
