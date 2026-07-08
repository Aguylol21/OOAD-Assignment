package model;

import java.time.LocalDate;

public class Rental {

    private String rentalId;
    private String userId;
    private String userName;
    private String userType;
    private String equipmentId;
    private String equipmentName;
    private LocalDate rentalDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private RentalStatus status;
    private int totalFee;

    public Rental(
            String rentalId,
            String userId,
            String userName,
            String userType,
            String equipmentId,
            String equipmentName,
            LocalDate rentalDate,
            LocalDate expectedReturnDate,
            LocalDate actualReturnDate,
            RentalStatus status,
            int totalFee
    ) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.totalFee = totalFee;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    public void markReturned(LocalDate returnDate) {
        actualReturnDate = returnDate;
        status = RentalStatus.RETURNED;
    }
}
