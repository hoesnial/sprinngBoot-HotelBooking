package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String addressLine;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Address() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id          The address ID.
     * @param addressLine The main address line.
     * @param city        The city.
     * @param country     The country.
     */
    public Address(Long id, String addressLine, String city, String country) {
        this.id = id;
        this.addressLine = addressLine;
        this.city = city;
        this.country = country;
    }

    // Constructor alternatif tanpa ID (karena ID biasanya di-generate)
    public Address(String addressLine, String city, String country) {
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

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "Address{" +
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
        Address address = (Address) o;
        // Untuk entitas JPA, kesetaraan seringkali didasarkan pada ID setelah persistensi.
        // Jika ID null (entitas baru), maka bisa berdasarkan field lain yang unik secara bisnis.
        // Implementasi saat ini menggunakan id dan addressLine.
        // Pertimbangkan untuk menyederhanakan hanya ke 'id' jika itu adalah praktik standar Anda.
        if (id != null) {
            return Objects.equals(id, address.id);
        }
        // Jika ID null, bandingkan berdasarkan konten field lainnya
        return Objects.equals(addressLine, address.addressLine) &&
               Objects.equals(city, address.city) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        // Harus konsisten dengan equals.
        // Jika equals hanya berdasarkan id (setelah persistensi), hashCode juga harus demikian.
        // Jika ID null, maka hash berdasarkan field lain.
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(addressLine, city, country);
    }
}