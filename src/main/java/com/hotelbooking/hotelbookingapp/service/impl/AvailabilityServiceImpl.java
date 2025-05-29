package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.Availability;
import com.hotelbooking.hotelbookingapp.model.Hotel;
import com.hotelbooking.hotelbookingapp.model.Room;
import com.hotelbooking.hotelbookingapp.model.dto.RoomSelectionDTO;
import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import com.hotelbooking.hotelbookingapp.repository.AvailabilityRepository;
import com.hotelbooking.hotelbookingapp.service.AvailabilityService;
import com.hotelbooking.hotelbookingapp.service.HotelService;
import com.hotelbooking.hotelbookingapp.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class AvailabilityServiceImpl implements AvailabilityService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(AvailabilityServiceImpl.class);

    private final AvailabilityRepository availabilityRepository;
    private final HotelService hotelService;
    private final RoomService roomService;

    // Manual constructor to replace @RequiredArgsConstructor
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   HotelService hotelService,
                                   RoomService roomService) {
        this.availabilityRepository = availabilityRepository;
        this.hotelService = hotelService;
        this.roomService = roomService;
    }

    @Override
    public Integer getMinAvailableRooms(Long roomId, LocalDate checkinDate, LocalDate checkoutDate) {
        log.info("Fetching minimum available rooms for room ID {} from {} to {}", roomId, checkinDate, checkoutDate);

        Room room = roomService.findRoomById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + roomId));

        // Fetch the minimum available rooms throughout the booking range for a room ID.
        return availabilityRepository.getMinAvailableRooms(roomId, checkinDate, checkoutDate)
                .orElse(room.getRoomCount()); // Consider no record as full availability
    }

    @Override
    public void updateAvailabilities(long hotelId, LocalDate checkinDate, LocalDate checkoutDate, List<RoomSelectionDTO> roomSelections) {
        Hotel hotel = hotelService.findHotelById(hotelId).orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + hotelId));
        log.info("Attempting to update availabilities for hotel ID {} from {} to {}", hotelId, checkinDate, checkoutDate);

        roomSelections = roomSelections.stream()
                .filter(roomSelection -> roomSelection.getCount() > 0)
                .collect(Collectors.toList());

        // Iterate through the room selections made by the user
        for (RoomSelectionDTO roomSelection : roomSelections) {
            RoomType roomType = roomSelection.getRoomType();
            int selectedCount = roomSelection.getCount();
            log.debug("Processing {} room(s) of type {}", selectedCount, roomType);

            // Find the room by roomType for the given hotel
            Room room = hotel.getRooms().stream()
                    .filter(r -> r.getRoomType() == roomType)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Room type " + roomType + " not found in hotel ID: " + hotelId));

            // Iterate through the dates and update or create availability
            for (LocalDate date = checkinDate; date.isBefore(checkoutDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date; // Temporary final variable
                Availability availability = availabilityRepository.findByRoomIdAndDate(room.getId(), date)
                        .orElseGet(() -> {
                            // Replaced .builder() with constructor and setters
                            Availability newAvailability = new Availability();
                            newAvailability.setHotel(hotel);
                            newAvailability.setDate(currentDate);
                            newAvailability.setRoom(room);
                            newAvailability.setAvailableRooms(room.getRoomCount());
                            return newAvailability;
                        });

                // Reduce the available rooms by the selected count
                int updatedAvailableRooms = availability.getAvailableRooms() - selectedCount;
                if (updatedAvailableRooms < 0) {
                    throw new IllegalArgumentException("Selected rooms exceed available rooms for date: " + currentDate + " and room type: " + roomType);
                }
                availability.setAvailableRooms(updatedAvailableRooms);

                availabilityRepository.save(availability);
            }
        }
        log.info("Successfully updated availabilities for hotel ID {} from {} to {}", hotelId, checkinDate, checkoutDate);
    }

}