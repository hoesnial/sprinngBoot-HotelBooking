package com.hotelbooking.hotelbookingapp.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Objects;

public class HotelSearchDTO {

    @NotBlank(message = "City cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z '-]+$", message = "City must only contain letters, apostrophes('), or hyphens(-)")
    private String city;

    @NotNull(message = "Check-in date cannot be empty")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkinDate;

    @NotNull(message = "Check-out date cannot be empty")
    private LocalDate checkoutDate;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public HotelSearchDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public HotelSearchDTO(String city, LocalDate checkinDate, LocalDate checkoutDate) {
        this.city = city;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    // --- Getters and Setters ---

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "HotelSearchDTO{" +
               "city='" + city + '\'' +
               ", checkinDate=" + checkinDate +
               ", checkoutDate=" + checkoutDate +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelSearchDTO that = (HotelSearchDTO) o;
        return Objects.equals(city, that.city) &&
               Objects.equals(checkinDate, that.checkinDate) &&
               Objects.equals(checkoutDate, that.checkoutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, checkinDate, checkoutDate);
    }
}