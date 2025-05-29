package com.hotelbooking.hotelbookingapp.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HotelAvailabilityDTO {

    private Long id;
    private String name;
    private AddressDTO addressDTO;
    private List<RoomDTO> roomDTOs = new ArrayList<>(); // Inisialisasi dipertahankan
    private Integer maxAvailableSingleRooms;
    private Integer maxAvailableDoubleRooms;

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'roomDTOs' dengan ArrayList kosong.
     */
    public HotelAvailabilityDTO() {
        // Inisialisasi 'roomDTOs' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     */
    public HotelAvailabilityDTO(Long id, String name, AddressDTO addressDTO,
                                List<RoomDTO> roomDTOs, Integer maxAvailableSingleRooms,
                                Integer maxAvailableDoubleRooms) {
        this.id = id;
        this.name = name;
        this.addressDTO = addressDTO;
        if (roomDTOs != null) { // Menghindari NullPointerException
            this.roomDTOs = roomDTOs;
        } else {
            this.roomDTOs = new ArrayList<>(); // Pastikan list selalu terinisialisasi
        }
        this.maxAvailableSingleRooms = maxAvailableSingleRooms;
        this.maxAvailableDoubleRooms = maxAvailableDoubleRooms;
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

    public Integer getMaxAvailableSingleRooms() {
        return maxAvailableSingleRooms;
    }

    public void setMaxAvailableSingleRooms(Integer maxAvailableSingleRooms) {
        this.maxAvailableSingleRooms = maxAvailableSingleRooms;
    }

    public Integer getMaxAvailableDoubleRooms() {
        return maxAvailableDoubleRooms;
    }

    public void setMaxAvailableDoubleRooms(Integer maxAvailableDoubleRooms) {
        this.maxAvailableDoubleRooms = maxAvailableDoubleRooms;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "HotelAvailabilityDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", addressDTO=" + (addressDTO != null ? addressDTO.getCity() : "null") + // Contoh: tampilkan kota
               ", roomDTOs=" + (roomDTOs != null ? roomDTOs.size() : 0) + // Tampilkan ukuran list
               ", maxAvailableSingleRooms=" + maxAvailableSingleRooms +
               ", maxAvailableDoubleRooms=" + maxAvailableDoubleRooms +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelAvailabilityDTO that = (HotelAvailabilityDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(addressDTO, that.addressDTO) &&
               Objects.equals(roomDTOs, that.roomDTOs) &&
               Objects.equals(maxAvailableSingleRooms, that.maxAvailableSingleRooms) &&
               Objects.equals(maxAvailableDoubleRooms, that.maxAvailableDoubleRooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, addressDTO, roomDTOs, maxAvailableSingleRooms, maxAvailableDoubleRooms);
    }
}