package com.hotelbooking.hotelbookingapp.model;

import java.util.ArrayList;
import java.util.List; // Pastikan import ini ada
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL) // Jika Address dimiliki sepenuhnya oleh Hotel
    private Address address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>(); // Inisialisasi langsung sebagai ArrayList

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>(); // Inisialisasi langsung sebagai ArrayList

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private HotelManager hotelManager;

    // --- Constructors ---
    public Hotel() {
        // Inisialisasi 'rooms' dan 'bookings' sudah dilakukan di deklarasi field.
        // Jika Anda ingin memastikan, bisa juga ditambahkan di sini:
        // this.rooms = new ArrayList<>();
        // this.bookings = new ArrayList<>();
    }

    public Hotel(Long id, String name, Address address, List<Room> rooms, List<Booking> bookings, HotelManager hotelManager) {
        this(); // Memanggil constructor default untuk inisialisasi list
        this.id = id;
        this.name = name;
        this.address = address;
        if (rooms != null) {
            this.rooms = rooms;
        }
        if (bookings != null) {
            this.bookings = bookings;
        }
        this.hotelManager = hotelManager;
    }

    public Hotel(String name, Address address, HotelManager hotelManager) {
        this(); // Memanggil constructor default untuk inisialisasi list
        this.name = name;
        this.address = address;
        this.hotelManager = hotelManager;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public HotelManager getHotelManager() {
        return hotelManager;
    }

    public void setHotelManager(HotelManager hotelManager) {
        this.hotelManager = hotelManager;
    }

    // --- Helper methods ---
    public void addRoom(Room room) {
        if (room != null) {
            if (this.rooms == null) { // Pengaman tambahan jika inisialisasi di atas terlewat
                this.rooms = new ArrayList<>();
            }
            this.rooms.add(room);
            room.setHotel(this);
        }
    }

    public void removeRoom(Room room) {
        if (room != null && this.rooms != null) {
            this.rooms.remove(room);
            room.setHotel(null);
        }
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            if (this.bookings == null) { // Pengaman tambahan
                this.bookings = new ArrayList<>();
            }
            this.bookings.add(booking);
            booking.setHotel(this);
        }
    }

    public void removeBooking(Booking booking) {
        if (booking != null && this.bookings != null) {
            this.bookings.remove(booking);
            booking.setHotel(null);
        }
    }

    // --- Overridden Object methods ---
    @Override
    public String toString() {
        return "Hotel{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", addressId=" + (address != null ? address.getId() : "null") +
               ", numberOfRooms=" + (rooms != null ? rooms.size() : 0) +
               ", numberOfBookings=" + (bookings != null ? bookings.size() : 0) +
               ", hotelManagerId=" + (hotelManager != null && hotelManager.getUser() != null ? hotelManager.getUser().getId() : "null") +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}