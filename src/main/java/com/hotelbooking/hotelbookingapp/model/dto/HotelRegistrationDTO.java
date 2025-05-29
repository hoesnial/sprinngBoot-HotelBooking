package com.hotelbooking.hotelbookingapp.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HotelRegistrationDTO {

    @NotBlank(message = "Hotel name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "Hotel name must only contain letters and numbers")
    private String name;

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private List<RoomDTO> roomDTOs = new ArrayList<>(); // Inisialisasi dipertahankan

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'roomDTOs' dengan ArrayList kosong.
     */
    public HotelRegistrationDTO() {
        // Inisialisasi 'roomDTOs' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     */
    public HotelRegistrationDTO(String name, AddressDTO addressDTO, List<RoomDTO> roomDTOs) {
        this.name = name;
        this.addressDTO = addressDTO;
        if (roomDTOs != null) { // Menghindari NullPointerException
            this.roomDTOs = roomDTOs;
        } else {
            this.roomDTOs = new ArrayList<>(); // Pastikan list selalu terinisialisasi
        }
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }

    public List<RoomDTO> getRoomDTOs() {
        return roomDTOs;
    }

    public void setRoomDTOs(List<RoomDTO> roomDTOs) {
        this.roomDTOs = roomDTOs;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "HotelRegistrationDTO{" +
               "name='" + name + '\'' +
               ", addressDTO=" + (addressDTO != null ? addressDTO.getCity() : "null") + // Contoh: tampilkan kota
               ", roomDTOs=" + (roomDTOs != null ? roomDTOs.size() : 0) + // Tampilkan ukuran list
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelRegistrationDTO that = (HotelRegistrationDTO) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(addressDTO, that.addressDTO) &&
               Objects.equals(roomDTOs, that.roomDTOs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addressDTO, roomDTOs);
    }
}