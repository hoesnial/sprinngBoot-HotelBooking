package com.hotelbooking.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

public class AddressDTO {

    private Long id;

    @NotBlank(message = "Address line cannot be empty")
    @Pattern(regexp = "^[A-Za-z0-9 .,:\\-]*$", message = "Address line can only contain letters, numbers, and some special characters (. , : - )")
    private String addressLine;

    @NotBlank(message = "City cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "City must only contain letters")
    private String city;

    @NotBlank(message = "Country cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Country must only contain letters")
    private String country;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public AddressDTO() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id          The address ID.
     * @param addressLine The main address line.
     * @param city        The city.
     * @param country     The country.
     */
    public AddressDTO(Long id, String addressLine, String city, String country) {
        this.id = id;
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "AddressDTO{" +
               "id=" + id +
               ", addressLine='" + addressLine + '\'' +
               ", city='" + city + '\'' +
               ", country='" + country + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDTO that = (AddressDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(addressLine, that.addressLine) &&
               Objects.equals(city, that.city) &&
               Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressLine, city, country);
    }
}