package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = false)
    private Room room;

    @Column(nullable = false)
    private int availableRooms;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Availability() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id             The availability ID.
     * @param hotel          The hotel this availability refers to.
     * @param date           The specific date of availability.
     * @param room           The room this availability refers to.
     * @param availableRooms The number of available rooms.
     */
    public Availability(Long id, Hotel hotel, LocalDate date, Room room, int availableRooms) {
        this.id = id;
        this.hotel = hotel;
        this.date = date;
        this.room = room;
        this.availableRooms = availableRooms;
    }

    /**
     * Constructor for essential fields, excluding id.
     *
     * @param hotel          The hotel this availability refers to.
     * @param date           The specific date of availability.
     * @param room           The room this availability refers to.
     * @param availableRooms The number of available rooms.
     */
    public Availability(Hotel hotel, LocalDate date, Room room, int availableRooms) {
        this.hotel = hotel;
        this.date = date;
        this.room = room;
        this.availableRooms = availableRooms;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "Availability{" +
               "id=" + id +
               ", hotelId=" + (hotel != null ? hotel.getId() : "null") + // Menampilkan hotelId
               ", date=" + date +
               ", roomId=" + (room != null ? room.getId() : "null") + // Menampilkan roomId
               ", availableRooms=" + availableRooms +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Availability that = (Availability) o;
        // Untuk entitas JPA, kesetaraan seringkali didasarkan pada ID setelah persistensi.
        // Jika ID null, maka bisa berdasarkan kombinasi field yang unik secara bisnis.
        if (id != null) {
            return Objects.equals(id, that.id);
        }
        // Untuk entitas baru (ID null), kesetaraan bisa didasarkan pada kunci bisnis alami
        // seperti kombinasi hotel, room, dan date.
        return Objects.equals(hotel, that.hotel) &&
               Objects.equals(date, that.date) &&
               Objects.equals(room, that.room);
    }

    @Override
    public int hashCode() {
        // Harus konsisten dengan equals.
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(hotel, date, room);
    }
}