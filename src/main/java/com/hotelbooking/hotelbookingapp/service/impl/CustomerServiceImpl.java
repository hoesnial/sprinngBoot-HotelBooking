package com.hotelbooking.hotelbookingapp.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelbookingapp.model.Customer;
import com.hotelbooking.hotelbookingapp.repository.CustomerRepository;
import com.hotelbooking.hotelbookingapp.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    // Inisialisasi Logger manual
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    // Constructor manual
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findByUserId(Long userId) {
        log.debug("Mencari customer dengan userId: {}", userId); // Logger digunakan di sini
        Optional<Customer> customer = customerRepository.findByUserId(userId);
        if (customer.isEmpty()) {
            log.warn("Customer dengan userId: {} tidak ditemukan", userId); // Contoh penggunaan logger lainnya
        } else {
            log.debug("Customer dengan userId: {} ditemukan", userId);
        }
        return customer;
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        log.debug("Mencari customer dengan username: {}", username); // Logger digunakan di sini
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isEmpty()) {
            log.warn("Customer dengan username: {} tidak ditemukan", username); // Contoh penggunaan logger lainnya
        } else {
            log.debug("Customer dengan username: {} ditemukan", username);
        }
        return customer;
    }

}