package com.hotelbooking.hotelbookingapp.model.enums;

// import lombok.AllArgsConstructor; // Dihapus
// import lombok.Getter; // Dihapus

// @Getter // Dihapus
// @AllArgsConstructor // Dihapus
public enum RoomType {

    SINGLE(1),
    DOUBLE(2);

    private final int capacity;

    // Constructor manual untuk menggantikan @AllArgsConstructor
    RoomType(int capacity) {
        this.capacity = capacity;
    }

    // Getter manual untuk menggantikan @Getter
    public int getCapacity() {
        return capacity;
    }
}