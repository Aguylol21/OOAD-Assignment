package service;

import model.Rental;

public interface DiscountPolicy {

    double calculateDiscount(Rental rental, double baseFee);

    String getDiscountType(Rental rental);

    double getDiscountRate(Rental rental);
}
