package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.Hotel;
import com.hotelbooking.hotelbookingapp.model.Room;
import com.hotelbooking.hotelbookingapp.model.dto.RoomDTO;
import com.hotelbooking.hotelbookingapp.repository.RoomRepository;
import com.hotelbooking.hotelbookingapp.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class RoomServiceImpl implements RoomService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;

    // Manual constructor to replace @RequiredArgsConstructor
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room saveRoom(RoomDTO roomDTO, Hotel hotel) {
        log.info("Attempting to save a new room for hotel ID {}: Room Type {}, Count {}", hotel.getId(), roomDTO.getRoomType(), roomDTO.getRoomCount());
        Room room = mapRoomDtoToRoom(roomDTO, hotel);
        room = roomRepository.save(room);
        log.info("Successfully saved room with ID: {} for hotel ID: {}", room.getId(), hotel.getId());
        return room;
    }

    @Override
    public List<Room> saveRooms(List<RoomDTO> roomDTOs, Hotel hotel) {
        log.info("Attempting to save {} rooms for hotel ID: {}", roomDTOs.size(), hotel.getId());
        List<Room> rooms = roomDTOs.stream()
                .map(roomDTO -> saveRoom(roomDTO, hotel)) // save each room
                .collect(Collectors.toList());
        log.info("Successfully saved {} rooms for hotel ID: {}", rooms.size(), hotel.getId());
        return rooms;
    }

    @Override
    public Optional<Room> findRoomById(Long id) {
        log.debug("Finding room by ID: {}", id);
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findRoomsByHotelId(Long hotelId) {
        // Implementation was null, leaving as is.
        // Example implementation:
        // log.debug("Finding rooms by hotel ID: {}", hotelId);
        // return roomRepository.findByHotelId(hotelId); // Assuming such a method exists in RoomRepository
        return null;
    }

    @Override
    public Room updateRoom(RoomDTO roomDTO) {
        log.info("Attempting to update room with ID: {}", roomDTO.getId());
        Room existingRoom = roomRepository.findById(roomDTO.getId())
                .orElseThrow(() -> {
                    log.warn("Room not found for update with ID: {}", roomDTO.getId());
                    return new EntityNotFoundException("Room not found with ID: " + roomDTO.getId());
                });

        log.info("Room with ID: {} found. Current details: Type={}, Count={}, Price={}",
                existingRoom.getId(), existingRoom.getRoomType(), existingRoom.getRoomCount(), existingRoom.getPricePerNight());

        existingRoom.setRoomType(roomDTO.getRoomType());
        existingRoom.setRoomCount(roomDTO.getRoomCount());
        existingRoom.setPricePerNight(roomDTO.getPricePerNight());
        // Note: The hotel association is not updated here. If hotelId in RoomDTO is meant to change the hotel,
        // that logic would need to be added, including fetching the new Hotel entity.

        Room updatedRoom = roomRepository.save(existingRoom);
        log.info("Successfully updated room with ID: {}. New details: Type={}, Count={}, Price={}",
                updatedRoom.getId(), updatedRoom.getRoomType(), updatedRoom.getRoomCount(), updatedRoom.getPricePerNight());
        return updatedRoom;
    }

    @Override
    public void deleteRoom(Long id) {
        // Implementation was empty, leaving as is.
        // Example implementation:
        // log.info("Attempting to delete room with ID: {}", id);
        // if (!roomRepository.existsById(id)) {
        //     log.warn("Attempt to delete non-existing room with ID: {}", id);
        //     throw new EntityNotFoundException("Room not found with ID: " + id);
        // }
        // roomRepository.deleteById(id);
        // log.info("Successfully deleted room with ID: {}", id);
    }

    @Override
    public Room mapRoomDtoToRoom(RoomDTO roomDTO, Hotel hotel) {
        log.debug("Mapping RoomDTO (Type: {}, Count: {}) to Room for Hotel ID: {}", roomDTO.getRoomType(), roomDTO.getRoomCount(), hotel.getId());
        // Replaced .builder() with constructor and setters
        Room room = new Room();
        room.setHotel(hotel); // Set the hotel association
        room.setRoomType(roomDTO.getRoomType());
        room.setRoomCount(roomDTO.getRoomCount());
        room.setPricePerNight(roomDTO.getPricePerNight());
        // ID is not set here as it's for a new or to-be-updated room where ID might be auto-generated
        // or comes from roomDTO.getId() if updating an existing one (though this method is more for creation).
        log.debug("Mapped Room entity: {}", room);
        return room;
    }

    @Override
    public RoomDTO mapRoomToRoomDto(Room room) {
        // Replaced .builder() with constructor and setters
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(room.getId());
        if (room.getHotel() != null) {
            roomDTO.setHotelId(room.getHotel().getId());
        }
        roomDTO.setRoomType(room.getRoomType());
        roomDTO.setRoomCount(room.getRoomCount());
        roomDTO.setPricePerNight(room.getPricePerNight());
        return roomDTO;
    }
}