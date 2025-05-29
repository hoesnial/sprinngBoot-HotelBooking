package com.hotelbooking.hotelbookingapp.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger; // Assuming HotelManagerService is still in this package if other services use it
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hotelbooking.hotelbookingapp.exception.HotelAlreadyExistsException;
import com.hotelbooking.hotelbookingapp.model.Address;
import com.hotelbooking.hotelbookingapp.model.Hotel;
import com.hotelbooking.hotelbookingapp.model.HotelManager;
import com.hotelbooking.hotelbookingapp.model.Room;
import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.model.dto.AddressDTO;
import com.hotelbooking.hotelbookingapp.model.dto.HotelDTO;
import com.hotelbooking.hotelbookingapp.model.dto.HotelRegistrationDTO;
import com.hotelbooking.hotelbookingapp.model.dto.RoomDTO;
import com.hotelbooking.hotelbookingapp.repository.HotelRepository;
import com.hotelbooking.hotelbookingapp.service.AddressService;
import com.hotelbooking.hotelbookingapp.service.HotelService;
import com.hotelbooking.hotelbookingapp.service.RoomService;
import com.hotelbooking.hotelbookingapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelServiceImpl.class);

    private final HotelRepository hotelRepository;
    private final AddressService addressService;
    private final RoomService roomService;
    private final UserService userService;
    // private final HotelManagerService hotelManagerService; // REMOVED

    public HotelServiceImpl(HotelRepository hotelRepository,
                            AddressService addressService,
                            RoomService roomService,
                            UserService userService) { // REMOVED HotelManagerService from parameters
        this.hotelRepository = hotelRepository;
        this.addressService = addressService;
        this.roomService = roomService;
        this.userService = userService;
        // this.hotelManagerService = hotelManagerService; // REMOVED assignment
    }

    @Override
    @Transactional
    public Hotel saveHotel(HotelRegistrationDTO hotelRegistrationDTO) {
        log.info("Attempting to save a new hotel: {}", hotelRegistrationDTO.getName());

        Optional<Hotel> existingHotel = hotelRepository.findByName(formatText(hotelRegistrationDTO.getName()));
        if (existingHotel.isPresent()) {
            log.warn("Hotel with name {} already exists.", hotelRegistrationDTO.getName());
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        Hotel hotel = mapHotelRegistrationDtoToHotel(hotelRegistrationDTO);

        AddressDTO addressDTO = hotelRegistrationDTO.getAddressDTO();
        if (addressDTO == null) {
            log.error("AddressDTO is null for hotel registration: {}", hotelRegistrationDTO.getName());
            throw new IllegalArgumentException("Address information cannot be null for hotel registration.");
        }
        Address savedAddress = addressService.saveAddress(addressDTO);
        hotel.setAddress(savedAddress);
        log.debug("Address saved and set for hotel: {}", hotel.getName());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username);
        if (user == null) { // It's good practice to check if user is null
            log.error("Authenticated user {} not found in the database.", username);
            throw new EntityNotFoundException("Current user not found in the database.");
        }
        if (user.getHotelManager() == null) {
            log.error("Hotel Manager details not found for current user: {}", username);
            throw new EntityNotFoundException("Hotel Manager details not found for the current user. Ensure the user is a Hotel Manager.");
        }
        HotelManager hotelManager = user.getHotelManager();
        hotel.setHotelManager(hotelManager);
        log.debug("HotelManager set for hotel: {}", hotel.getName());

        hotel = hotelRepository.save(hotel);
        log.debug("Initial save for hotel {} complete, ID: {}.", hotel.getName(), hotel.getId());

        if (hotelRegistrationDTO.getRoomDTOs() == null || hotelRegistrationDTO.getRoomDTOs().isEmpty()) {
            log.warn("No room DTOs provided for hotel registration: {}", hotelRegistrationDTO.getName());
        } else {
            List<Room> savedRooms = roomService.saveRooms(hotelRegistrationDTO.getRoomDTOs(), hotel);
            hotel.setRooms(savedRooms);
            log.debug("Rooms saved and set for hotel: {}", hotel.getName());
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Successfully saved new hotel with ID: {}", savedHotel.getId());
        return savedHotel;
    }

    // ... (rest of the methods remain the same, as they don't use hotelManagerService) ...
    // Make sure no other method was using hotelManagerService. If so, that logic would also need review.

    @Override
    public HotelDTO findHotelDtoByName(String name) {
        log.debug("Finding hotel DTO by name: {}", name);
        Hotel hotel = hotelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with name: " + name));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    public HotelDTO findHotelDtoById(Long id) {
        log.debug("Finding hotel DTO by ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + id));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    public Optional<Hotel> findHotelById(Long id) {
        log.debug("Finding hotel entity by ID: {}", id);
        return hotelRepository.findById(id);
    }

    @Override
    public List<HotelDTO> findAllHotels() {
        log.debug("Finding all hotels");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::mapHotelToHotelDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelDTO updateHotel(HotelDTO hotelDTO) {
        log.info("Attempting to update hotel with ID: {}", hotelDTO.getId());

        Hotel existingHotel = hotelRepository.findById(hotelDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + hotelDTO.getId()));

        if (hotelNameExistsAndNotSameHotel(hotelDTO.getName(), hotelDTO.getId())) {
            log.warn("Attempt to update hotel ID {} with a name '{}' that already exists for another hotel.", hotelDTO.getId(), hotelDTO.getName());
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        existingHotel.setName(formatText(hotelDTO.getName()));

        if (hotelDTO.getAddressDTO() == null) {
            log.error("AddressDTO is null for hotel update: ID {}", hotelDTO.getId());
            throw new IllegalArgumentException("Address information cannot be null for hotel update.");
        }
        if (existingHotel.getAddress() != null && hotelDTO.getAddressDTO().getId() == null) {
            hotelDTO.getAddressDTO().setId(existingHotel.getAddress().getId());
        }
        Address updatedAddress = addressService.updateAddress(hotelDTO.getAddressDTO());
        existingHotel.setAddress(updatedAddress);
        log.debug("Address updated for hotel ID: {}", hotelDTO.getId());

        if (hotelDTO.getRoomDTOs() != null) {
            hotelDTO.getRoomDTOs().forEach(roomDTO -> {
                if (roomDTO.getId() != null) {
                    roomService.updateRoom(roomDTO);
                } else {
                    log.warn("Skipping update for room DTO without ID during hotel update: {}", roomDTO.getRoomType());
                }
            });
            log.debug("Rooms processed for update for hotel ID: {}", hotelDTO.getId());
        }

        hotelRepository.save(existingHotel);
        log.info("Successfully updated existing hotel with ID: {}", hotelDTO.getId());
        return mapHotelToHotelDto(existingHotel);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        log.info("Attempting to delete hotel with ID: {}", id);
        if (!hotelRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing hotel with ID: {}", id);
            throw new EntityNotFoundException("Hotel not found with ID: " + id);
        }
        hotelRepository.deleteById(id);
        log.info("Successfully deleted hotel with ID: {}", id);
    }
    @Override
    public List<Hotel> findAllHotelsByManagerId(Long managerId) {
        log.debug("Finding all hotel entities for manager ID: {}", managerId);
        List<Hotel> hotels = hotelRepository.findAllByHotelManager_Id(managerId);
        return (hotels != null) ? hotels : Collections.emptyList();
    }

    @Override
    public List<HotelDTO> findAllHotelDtosByManagerId(Long managerId) {
        log.debug("Finding all hotel DTOs for manager ID: {}", managerId);
        List<Hotel> hotels = hotelRepository.findAllByHotelManager_Id(managerId);
        if (hotels != null) {
            return hotels.stream()
                    .map(this::mapHotelToHotelDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public HotelDTO findHotelByIdAndManagerId(Long hotelId, Long managerId) {
        log.debug("Finding hotel DTO by ID: {} for manager ID: {}", hotelId, managerId);
        Hotel hotel = hotelRepository.findByIdAndHotelManager_Id(hotelId, managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + hotelId + " for manager ID: " + managerId));
        return mapHotelToHotelDto(hotel);
    }

    @Override
    @Transactional
    public HotelDTO updateHotelByManagerId(HotelDTO hotelDTO, Long managerId) {
        log.info("Attempting to update hotel with ID: {} for Manager ID: {}", hotelDTO.getId(), managerId);

        Hotel existingHotel = hotelRepository.findByIdAndHotelManager_Id(hotelDTO.getId(), managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + hotelDTO.getId() + " for manager ID: " + managerId));

        if (hotelNameExistsAndNotSameHotel(hotelDTO.getName(), hotelDTO.getId())) {
            log.warn("Attempt to update hotel ID {} (manager ID {}) with a name '{}' that already exists for another hotel.", hotelDTO.getId(), managerId, hotelDTO.getName());
            throw new HotelAlreadyExistsException("This hotel name is already registered!");
        }

        existingHotel.setName(formatText(hotelDTO.getName()));

        if (hotelDTO.getAddressDTO() == null) {
            log.error("AddressDTO is null for hotel update: ID {} (manager ID {})", hotelDTO.getId(), managerId);
            throw new IllegalArgumentException("Address information cannot be null for hotel update.");
        }
        if (existingHotel.getAddress() != null && hotelDTO.getAddressDTO().getId() == null) {
            hotelDTO.getAddressDTO().setId(existingHotel.getAddress().getId());
        }
        Address updatedAddress = addressService.updateAddress(hotelDTO.getAddressDTO());
        existingHotel.setAddress(updatedAddress);
        log.debug("Address updated for hotel ID: {} (manager ID {})", hotelDTO.getId(), managerId);


        if (hotelDTO.getRoomDTOs() != null) {
            hotelDTO.getRoomDTOs().forEach(roomDTO -> {
                 if (roomDTO.getId() != null) {
                    if (existingHotel.getRooms().stream().anyMatch(r -> r.getId().equals(roomDTO.getId()))) {
                        roomService.updateRoom(roomDTO);
                    } else {
                        log.warn("Room DTO with ID {} does not belong to hotel ID {} (manager ID {}). Skipping update.", roomDTO.getId(), hotelDTO.getId(), managerId);
                    }
                } else {
                    log.warn("Skipping update for room DTO without ID during hotel update for manager: {}", roomDTO.getRoomType());
                }
            });
            log.debug("Rooms processed for update for hotel ID: {} (manager ID {})", hotelDTO.getId(), managerId);
        }

        hotelRepository.save(existingHotel);
        log.info("Successfully updated existing hotel with ID: {} for Manager ID: {}", hotelDTO.getId(), managerId);
        return mapHotelToHotelDto(existingHotel);
    }

    @Override
    @Transactional
    public void deleteHotelByIdAndManagerId(Long hotelId, Long managerId) {
        log.info("Attempting to delete hotel with ID: {} for Manager ID: {}", hotelId, managerId);
        Hotel hotel = hotelRepository.findByIdAndHotelManager_Id(hotelId, managerId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + hotelId + " for manager ID: " + managerId));
        hotelRepository.delete(hotel);
        log.info("Successfully deleted hotel with ID: {} for Manager ID: {}", hotelId, managerId);
    }

    private Hotel mapHotelRegistrationDtoToHotel(HotelRegistrationDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setName(formatText(dto.getName()));
        return hotel;
    }

    @Override
    public HotelDTO mapHotelToHotelDto(Hotel hotel) {
        List<RoomDTO> roomDTOs = Collections.emptyList();
        if (hotel.getRooms() != null) {
            roomDTOs = hotel.getRooms().stream()
                    .map(roomService::mapRoomToRoomDto)
                    .collect(Collectors.toList());
        }

        AddressDTO addressDTO = null;
        if (hotel.getAddress() != null) {
            addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());
        }

        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(hotel.getId());
        hotelDTO.setName(hotel.getName());
        hotelDTO.setAddressDTO(addressDTO);
        hotelDTO.setRoomDTOs(roomDTOs);
        if (hotel.getHotelManager() != null && hotel.getHotelManager().getUser() != null) {
            hotelDTO.setManagerUsername(hotel.getHotelManager().getUser().getUsername());
        }

        return hotelDTO;
    }

    private boolean hotelNameExistsAndNotSameHotel(String name, Long hotelId) {
        if (name == null) return false;
        Optional<Hotel> existingHotelWithSameName = hotelRepository.findByName(formatText(name));
        return existingHotelWithSameName.isPresent() && !existingHotelWithSameName.get().getId().equals(hotelId);
    }

    private String formatText(String text) {
        if (text == null) {
            return null;
        }
        return StringUtils.capitalize(text.trim());
    }
}