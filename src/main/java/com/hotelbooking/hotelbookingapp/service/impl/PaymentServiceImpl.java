package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.Booking;
import com.hotelbooking.hotelbookingapp.model.Payment;
import com.hotelbooking.hotelbookingapp.model.dto.BookingInitiationDTO;
import com.hotelbooking.hotelbookingapp.model.enums.Currency;
import com.hotelbooking.hotelbookingapp.model.enums.PaymentMethod;
import com.hotelbooking.hotelbookingapp.model.enums.PaymentStatus;
import com.hotelbooking.hotelbookingapp.repository.PaymentRepository;
import com.hotelbooking.hotelbookingapp.service.PaymentService;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class PaymentServiceImpl implements PaymentService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    // Manual constructor to replace @RequiredArgsConstructor
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking) {
        log.info("Attempting to save payment for booking ID: {}", booking.getId());

        // Replaced .builder() with constructor and setters
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTotalPrice(bookingInitiationDTO.getTotalPrice());
        payment.setPaymentStatus(PaymentStatus.COMPLETED); // Assuming the payment is completed
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD); // Default to CREDIT_CARD
        payment.setCurrency(Currency.USD); // Default to USD
        // transactionId and paymentDate are usually set by @PrePersist and @CreationTimestamp in the Payment entity

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved with transaction ID: {}", savedPayment.getTransactionId());

        return savedPayment;
    }
}