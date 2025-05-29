package com.hotelbooking.hotelbookingapp.service;

import com.hotelbooking.hotelbookingapp.model.Booking;
import com.hotelbooking.hotelbookingapp.model.Payment;
import com.hotelbooking.hotelbookingapp.model.dto.BookingInitiationDTO;

public interface PaymentService {

    Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking);
}
