package com.hotelbooking.hotelbookingapp.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookingInitiationDTO {

    private long hotelId;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private long durationDays;
    private List<RoomSelectionDTO> roomSelections = new ArrayList<>(); // Inisialisasi dipertahankan
    private BigDecimal totalPrice;

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'roomSelections' dengan ArrayList kosong.
     */
    public BookingInitiationDTO() {
        // Inisialisasi 'roomSelections' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     */
    public BookingInitiationDTO(long hotelId, LocalDate checkinDate, LocalDate checkoutDate,
                                long durationDays, List<RoomSelectionDTO> roomSelections, BigDecimal totalPrice) {
        this.hotelId = hotelId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.durationDays = durationDays;
        if (roomSelections != null) { // Menghindari NullPointerException
            this.roomSelections = roomSelections;
        } else {
            this.roomSelections = new ArrayList<>(); // Pastikan list selalu terinisialisasi
        }
        this.totalPrice = totalPrice;
    }

    // --- Getters and Setters ---

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public long getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(long durationDays) {
        this.durationDays = durationDays;
    }

    public List<RoomSelectionDTO> getRoomSelections() {
        return roomSelections;
    }

    public void setRoomSelections(List<RoomSelectionDTO> roomSelections) {
        this.roomSelections = roomSelections;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "BookingInitiationDTO{" +
               "hotelId=" + hotelId +
               ", checkinDate=" + checkinDate +
               ", checkoutDate=" + checkoutDate +
               ", durationDays=" + durationDays +
               ", roomSelections=" + (roomSelections != null ? roomSelections.size() : 0) + // Tampilkan ukuran untuk keringkasan
               ", totalPrice=" + totalPrice +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingInitiationDTO that = (BookingInitiationDTO) o;
        return hotelId == that.hotelId &&
               durationDays == that.durationDays &&
               Objects.equals(checkinDate, that.checkinDate) &&
               Objects.equals(checkoutDate, that.checkoutDate) &&
               Objects.equals(roomSelections, that.roomSelections) &&
               Objects.equals(totalPrice, that.totalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelId, checkinDate, checkoutDate, durationDays, roomSelections, totalPrice);
    }
}