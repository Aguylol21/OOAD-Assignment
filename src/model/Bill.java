package model;

public class Bill {

    private Rental rental;
    private Equipment equipment;
    private int rentalDuration;
    private double baseFee;
    private String discountType;
    private double discountRate;
    private double discountAmount;
    private int lateDays;
    private double latePenalty;
    private boolean damaged;
    private double damagePenalty;
    private double totalPenalty;
    private double netPayable;

    public Bill(
            Rental rental,
            Equipment equipment,
            int rentalDuration,
            double baseFee,
            String discountType,
            double discountRate,
            double discountAmount,
            int lateDays,
            double latePenalty,
            boolean damaged,
            double damagePenalty
    ) {
        this.rental = rental;
        this.equipment = equipment;
        this.rentalDuration = rentalDuration;
        this.baseFee = baseFee;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.discountAmount = discountAmount;
        this.lateDays = lateDays;
        this.latePenalty = latePenalty;
        this.damaged = damaged;
        this.damagePenalty = damagePenalty;
        this.totalPenalty = latePenalty + damagePenalty;
        this.netPayable = baseFee - discountAmount + totalPenalty;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public int getLateDays() {
        return lateDays;
    }

    public double getLatePenalty() {
        return latePenalty;
    }

    public double getDamagePenalty() {
        return damagePenalty;
    }

    public double getTotalPenalty() {
        return totalPenalty;
    }

    public double getNetPayable() {
        return netPayable;
    }

    public String generateBillText() {
        return """
                Rental ID: %s
                User ID: %s
                User Name: %s
                User Type: %s

                Equipment ID: %s
                Equipment Name: %s
                Category: %s
                Daily Rental Rate: RM%.2f

                Rental Date: %s
                Expected Return Date: %s
                Actual Return Date: %s
                Rental Duration: %d day(s)

                Base Rental Fee: RM%.2f
                Discount Applied: %s
                Discount Amount: RM%.2f

                Late Days: %d
                Late Penalty: RM%.2f
                Damaged Equipment: %s
                Damage Penalty: RM%.2f
                Total Penalty: RM%.2f

                Net Payable: RM%.2f
                """.formatted(
                rental.getRentalId(),
                rental.getUserId(),
                rental.getUserName(),
                rental.getUserType(),
                equipment.getEquipmentId(),
                equipment.getName(),
                equipment.getCategory(),
                (double) equipment.getDailyRate(),
                rental.getRentalDate(),
                rental.getExpectedReturnDate(),
                rental.getActualReturnDate() == null ? "-" : rental.getActualReturnDate(),
                rentalDuration,
                baseFee,
                getDiscountDisplay(),
                discountAmount,
                lateDays,
                latePenalty,
                damaged ? "Yes" : "No",
                damagePenalty,
                totalPenalty,
                netPayable
        );
    }

    public String generateInvoiceText() {
        return """
                PAYMENT SUMMARY / INVOICE

                Rental ID: %s
                User Name: %s
                User Type: %s
                Equipment: %s

                Base Rental Fee: RM%.2f
                Discount: RM%.2f
                Total Penalty: RM%.2f

                Net Payable: RM%.2f
                Payment Status: UNPAID
                """.formatted(
                rental.getRentalId(),
                rental.getUserName(),
                rental.getUserType(),
                equipment.getName(),
                baseFee,
                discountAmount,
                totalPenalty,
                netPayable
        );
    }

    private String getDiscountDisplay() {
        if (discountRate == 0) {
            return discountType;
        }

        return discountType + " (" + String.format("%.0f", discountRate * 100) + "%)";
    }
}
