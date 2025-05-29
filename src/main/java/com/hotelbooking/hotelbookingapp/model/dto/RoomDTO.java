package com.hotelbooking.hotelbookingapp.model.dto;

import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

public class RoomDTO {

    private Long id;
    private Long hotelId;
    private RoomType roomType;

    @NotNull(message = "Room count cannot be empty")
    @PositiveOrZero(message = "Room count must be 0 or more")
    private Integer roomCount;

    @NotNull(message = "Price cannot be empty")
    @PositiveOrZero(message = "Price per night must be 0 or more")
    private Double pricePerNight;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public RoomDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public RoomDTO(Long id, Long hotelId, RoomType roomType, Integer roomCount, Double pricePerNight) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.roomCount = roomCount;
        this.pricePerNight = pricePerNight;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Integer getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(Integer roomCount) {
        this.roomCount = roomCount;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "RoomDTO{" +
               "id=" + id +
               ", hotelId=" + hotelId +
               ", roomType=" + roomType +
               ", roomCount=" + roomCount +
               ", pricePerNight=" + pricePerNight +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomDTO roomDTO = (RoomDTO) o;
        return Objects.equals(id, roomDTO.id) &&
               Objects.equals(hotelId, roomDTO.hotelId) &&
               roomType == roomDTO.roomType &&
               Objects.equals(roomCount, roomDTO.roomCount) &&
               Objects.equals(pricePerNight, roomDTO.pricePerNight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hotelId, roomType, roomCount, pricePerNight);
    }
}