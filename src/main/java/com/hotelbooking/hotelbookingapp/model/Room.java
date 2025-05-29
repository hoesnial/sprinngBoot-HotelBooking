package com.hotelbooking.hotelbookingapp.model;

import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY untuk ManyToOne
    @JoinColumn(nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Sebaiknya roomType juga tidak null
    private RoomType roomType;

    @Column(nullable = false) // Sebaiknya roomCount juga tidak null
    private int roomCount;

    @Column(nullable = false) // Sebaiknya pricePerNight juga tidak null
    private double pricePerNight;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Availability> availabilities = new ArrayList<>(); // Inisialisasi default dipertahankan

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'availabilities' dengan ArrayList kosong.
     */
    public Room() {
        // Inisialisasi 'availabilities' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     *
     * @param id             The room's ID.
     * @param hotel          The hotel this room belongs to.
     * @param roomType       The type of the room.
     * @param roomCount      The number of such rooms.
     * @param pricePerNight  The price per night for this room type.
     * @param availabilities The list of availabilities for this room.
     */
    public Room(Long id, Hotel hotel, RoomType roomType, int roomCount, double pricePerNight, List<Availability> availabilities) {
        this.id = id;
        this.hotel = hotel;
        this.roomType = roomType;
        this.roomCount = roomCount;
        this.pricePerNight = pricePerNight;
        if (availabilities != null) { // Menghindari NullPointerException
            this.availabilities = availabilities;
        }
    }

    /**
     * Constructor for essential fields, excluding id and availabilities (which starts empty).
     *
     * @param hotel         The hotel this room belongs to.
     * @param roomType      The type of the room.
     * @param roomCount     The number of such rooms.
     * @param pricePerNight The price per night for this room type.
     */
    public Room(Hotel hotel, RoomType roomType, int roomCount, double pricePerNight) {
        this.hotel = hotel;
        this.roomType = roomType;
        this.roomCount = roomCount;
        this.pricePerNight = pricePerNight;
        // 'availabilities' akan menggunakan nilai default (ArrayList kosong)
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

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Availability> availabilities) {
        this.availabilities = availabilities;
    }

    // --- Helper methods for managing availabilities (opsional tapi direkomendasikan) ---
    public void addAvailability(Availability availability) {
        if (availability != null) {
            this.availabilities.add(availability);
            availability.setRoom(this); // Menjaga konsistensi dua arah
        }
    }

    public void removeAvailability(Availability availability) {
        if (availability != null) {
            this.availabilities.remove(availability);
            availability.setRoom(null); // Menjaga konsistensi dua arah
        }
    }

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "Room{" +
               "id=" + id +
               ", hotelId=" + (hotel != null ? hotel.getId() : "null") + // Menampilkan hotelId
               ", roomType=" + roomType +
               ", roomCount=" + roomCount +
               ", pricePerNight=" + pricePerNight +
               ", numberOfAvailabilities=" + (availabilities != null ? availabilities.size() : 0) +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        // Untuk entitas JPA, kesetaraan seringkali didasarkan pada ID setelah persistensi.
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        // Konsisten dengan equals. Jika equals hanya berdasarkan 'id', hashCode juga harus demikian.
        return Objects.hash(id);
    }
}