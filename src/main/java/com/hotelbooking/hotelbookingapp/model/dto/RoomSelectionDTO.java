package com.hotelbooking.hotelbookingapp.model.dto;

import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import java.util.Objects;

public class RoomSelectionDTO {

    private RoomType roomType;
    private int count;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public RoomSelectionDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public RoomSelectionDTO(RoomType roomType, int count) {
        this.roomType = roomType;
        this.count = count;
    }

    // --- Getters and Setters ---

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "RoomSelectionDTO{" +
               "roomType=" + roomType +
               ", count=" + count +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomSelectionDTO that = (RoomSelectionDTO) o;
        return count == that.count && roomType == that.roomType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomType, count);
    }
}