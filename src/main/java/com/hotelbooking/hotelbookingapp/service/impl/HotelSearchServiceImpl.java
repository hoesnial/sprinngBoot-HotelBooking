package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.service.AvailabilityService;
import com.hotelbooking.hotelbookingapp.service.AddressService;
import com.hotelbooking.hotelbookingapp.service.RoomService;
import com.hotelbooking.hotelbookingapp.service.HotelSearchService;
import com.hotelbooking.hotelbookingapp.model.Hotel;
import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import com.hotelbooking.hotelbookingapp.model.dto.AddressDTO;
import com.hotelbooking.hotelbookingapp.model.dto.HotelAvailabilityDTO;
import com.hotelbooking.hotelbookingapp.model.dto.RoomDTO;
import com.hotelbooking.hotelbookingapp.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class HotelSearchServiceImpl implements HotelSearchService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(HotelSearchServiceImpl.class);

    private final HotelRepository hotelRepository;
    private final AddressService addressService;
    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    // Manual constructor to replace @RequiredArgsConstructor
    public HotelSearchServiceImpl(HotelRepository hotelRepository,
                                  AddressService addressService,
                                  RoomService roomService,
                                  AvailabilityService availabilityService) {
        this.hotelRepository = hotelRepository;
        this.addressService = addressService;
        this.roomService = roomService;
        this.availabilityService = availabilityService;
    }

    @Override
    public List<HotelAvailabilityDTO> findAvailableHotelsByCityAndDate(String city, LocalDate checkinDate, LocalDate checkoutDate) {
        validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

        log.info("Attempting to find hotels in {} with available rooms from {} to {}", city, checkinDate, checkoutDate);

        // Number of days between check-in and check-out
        Long numberOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        if (numberOfDays <= 0) { // Added check for invalid date range resulting in non-positive days
            log.warn("Invalid date range: check-out date must be after check-in date, resulting in a positive number of days. Days calculated: {}", numberOfDays);
            throw new IllegalArgumentException("Check-out date must be after check-in date, resulting in at least one day of stay.");
        }


        // 1. Fetch hotels that satisfy the criteria (min 1 available room throughout the booking range)
        List<Hotel> hotelsWithAvailableRooms = hotelRepository.findHotelsWithAvailableRooms(city, checkinDate, checkoutDate, numberOfDays);
        log.debug("Found {} hotels with consistent availability records across the date range.", hotelsWithAvailableRooms.size());

        // 2. Fetch hotels that don't have any availability records for the entire booking range (considered fully available by default)
        List<Hotel> hotelsWithoutAvailabilityRecords = hotelRepository.findHotelsWithoutAvailabilityRecords(city, checkinDate, checkoutDate);
        log.debug("Found {} hotels without any availability records (assumed fully available).", hotelsWithoutAvailabilityRecords.size());

        // 3. Fetch hotels with partial availability; some days with records meeting the criteria and some days without any records
        List<Hotel> hotelsWithPartialAvailabilityRecords = hotelRepository.findHotelsWithPartialAvailabilityRecords(city, checkinDate, checkoutDate, numberOfDays);
        log.debug("Found {} hotels with partial availability records (mix of records and no records).", hotelsWithPartialAvailabilityRecords.size());


        // Combine and deduplicate the hotels using a Set
        Set<Hotel> combinedHotels = new HashSet<>(hotelsWithAvailableRooms);
        combinedHotels.addAll(hotelsWithoutAvailabilityRecords);
        combinedHotels.addAll(hotelsWithPartialAvailabilityRecords);

        log.info("Successfully found {} unique hotels considering all availability scenarios", combinedHotels.size());

        // Convert the combined hotel list to DTOs for the response
        return combinedHotels.stream()
                .map(hotel -> mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate))
                .collect(Collectors.toList());
    }

    @Override
    public HotelAvailabilityDTO findAvailableHotelById(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate) {
        validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

        log.info("Attempting to find hotel with ID {} with available rooms from {} to {}", hotelId, checkinDate, checkoutDate);

        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            log.error("No hotel found with ID: {}", hotelId);
            throw new EntityNotFoundException("Hotel not found with ID: " + hotelId);
        }

        Hotel hotel = hotelOptional.get();
        return mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate);
    }


    @Override
    public HotelAvailabilityDTO mapHotelToHotelAvailabilityDto(Hotel hotel, LocalDate checkinDate, LocalDate checkoutDate) {
        List<RoomDTO> roomDTOs = hotel.getRooms().stream()
                .map(roomService::mapRoomToRoomDto)  // convert each Room to RoomDTO
                .collect(Collectors.toList());

        AddressDTO addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());

        // Replaced .builder() with constructor and setters for HotelAvailabilityDTO
        HotelAvailabilityDTO hotelAvailabilityDTO = new HotelAvailabilityDTO();
        hotelAvailabilityDTO.setId(hotel.getId());
        hotelAvailabilityDTO.setName(hotel.getName());
        hotelAvailabilityDTO.setAddressDTO(addressDTO);
        hotelAvailabilityDTO.setRoomDTOs(roomDTOs);

        // For each room type, find the minimum available rooms across the date range
        int maxAvailableSingleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.SINGLE)
                .mapToInt(room -> {
                    Integer minAvailable = availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate);
                    log.trace("Hotel ID: {}, Room ID: {}, Type: SINGLE, Min Available ({}-{}): {}", hotel.getId(), room.getId(), checkinDate, checkoutDate, minAvailable);
                    return minAvailable;
                })
                .max()
                .orElse(0); // Assume no single rooms if none match the filter or no availability found
        hotelAvailabilityDTO.setMaxAvailableSingleRooms(maxAvailableSingleRooms);
        log.debug("Hotel ID: {}, Max Available SINGLE Rooms: {}", hotel.getId(), maxAvailableSingleRooms);


        int maxAvailableDoubleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.DOUBLE)
                .mapToInt(room -> {
                    Integer minAvailable = availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate);
                    log.trace("Hotel ID: {}, Room ID: {}, Type: DOUBLE, Min Available ({}-{}): {}", hotel.getId(), room.getId(), checkinDate, checkoutDate, minAvailable);
                    return minAvailable;
                })
                .max()
                .orElse(0); // Assume no double rooms if none match the filter or no availability found
        hotelAvailabilityDTO.setMaxAvailableDoubleRooms(maxAvailableDoubleRooms);
        log.debug("Hotel ID: {}, Max Available DOUBLE Rooms: {}", hotel.getId(), maxAvailableDoubleRooms);


        return hotelAvailabilityDTO;
    }

    private void validateCheckinAndCheckoutDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate == null || checkoutDate == null) {
            throw new IllegalArgumentException("Check-in and Check-out dates cannot be null.");
        }
        if (checkinDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!checkoutDate.isAfter(checkinDate)) { // Check-out must be strictly after check-in
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }
}