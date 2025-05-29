package com.hotelbooking.hotelbookingapp.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HotelDTO {

    private Long id;

    @NotBlank(message = "Hotel name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "Hotel name must only contain letters and numbers")
    private String name;

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private List<RoomDTO> roomDTOs = new ArrayList<>(); // Inisialisasi dipertahankan

    private String managerUsername;

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'roomDTOs' dengan ArrayList kosong.
     */
    public HotelDTO() {
        // Inisialisasi 'roomDTOs' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     */
    public HotelDTO(Long id, String name, AddressDTO addressDTO, List<RoomDTO> roomDTOs, String managerUsername) {
        this.id = id;
        this.name = name;
        this.addressDTO = addressDTO;
        if (roomDTOs != null) { // Menghindari NullPointerException
            this.roomDTOs = roomDTOs;
        } else {
            this.roomDTOs = new ArrayList<>(); // Pastikan list selalu terinisialisasi
        }
        this.managerUsername = managerUsername;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getManagerUsername() {
        return managerUsername;
    }

    public void setManagerUsername(String managerUsername) {
        this.managerUsername = managerUsername;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "HotelDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", addressDTO=" + (addressDTO != null ? addressDTO.getCity() : "null") + // Contoh: tampilkan kota
               ", roomDTOs=" + (roomDTOs != null ? roomDTOs.size() : 0) + // Tampilkan ukuran list
               ", managerUsername='" + managerUsername + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelDTO hotelDTO = (HotelDTO) o;
        return Objects.equals(id, hotelDTO.id) &&
               Objects.equals(name, hotelDTO.name) &&
               Objects.equals(addressDTO, hotelDTO.addressDTO) &&
               Objects.equals(roomDTOs, hotelDTO.roomDTOs) &&
               Objects.equals(managerUsername, hotelDTO.managerUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, addressDTO, roomDTOs, managerUsername);
    }
}