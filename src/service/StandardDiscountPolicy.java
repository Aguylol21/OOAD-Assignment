package service;

import model.Rental;
import model.Staff;
import model.Student;
import model.User;

import java.time.temporal.ChronoUnit;

public class StandardDiscountPolicy implements DiscountPolicy {

    @Override
    public double calculateDiscount(Rental rental, double baseFee) {
        return baseFee * getDiscountRate(rental);
    }

    @Override
    public String getDiscountType(Rental rental) {
        String userType = rental.getUserType();
        double discountRate = getDiscountRate(rental);

        if (discountRate == 0) {
            return "None";
        }

        if ("Staff".equalsIgnoreCase(userType)) {
            return "Staff Discount";
        }

        if ("Final-Year Student".equalsIgnoreCase(userType)) {
            return "Final-Year Student Discount";
        }

        return "Long Rental Promotion";
    }

    @Override
    public double getDiscountRate(Rental rental) {
        User user = createUser(rental);
        double discountRate = user == null ? 0 : user.getDiscountRate();

        int rentalDuration = (int) ChronoUnit.DAYS.between(
                rental.getRentalDate(),
                rental.getExpectedReturnDate()
        );

        if (rentalDuration >= 7 && 0.05 > discountRate) {
            discountRate = 0.05;
        }

        return discountRate;
    }

    private User createUser(Rental rental) {
        String userType = rental.getUserType();

        if ("Staff".equalsIgnoreCase(userType)) {
            return new Staff(
                    rental.getUserId(),
                    rental.getUserName()
            );
        }

        if ("Final-Year Student".equalsIgnoreCase(userType)) {
            return new Student(
                    rental.getUserId(),
                    rental.getUserName(),
                    true
            );
        }

        if ("Student".equalsIgnoreCase(userType)) {
            return new Student(
                    rental.getUserId(),
                    rental.getUserName(),
                    false
            );
        }

        return null;
    }
}
