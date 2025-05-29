package com.hotelbooking.hotelbookingapp.service;

import com.hotelbooking.hotelbookingapp.model.Customer;

import java.util.Optional;

public interface CustomerService {

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByUsername(String username);
}
