package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.HotelManager;
import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.repository.HotelManagerRepository;
import com.hotelbooking.hotelbookingapp.service.HotelManagerService;
import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class HotelManagerServiceImpl implements HotelManagerService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(HotelManagerServiceImpl.class);

    private final HotelManagerRepository hotelManagerRepository;

    // Manual constructor to replace @RequiredArgsConstructor
    public HotelManagerServiceImpl(HotelManagerRepository hotelManagerRepository) {
        this.hotelManagerRepository = hotelManagerRepository;
    }

    @Override
    public HotelManager findByUser(User user) {
        log.info("Attempting to find HotelManager for user ID: {}", user.getId());
        return hotelManagerRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("HotelManager not found for user {} (ID: {})", user.getUsername(), user.getId());
                    return new EntityNotFoundException("HotelManager not found for user " + user.getUsername());
                });
    }
}