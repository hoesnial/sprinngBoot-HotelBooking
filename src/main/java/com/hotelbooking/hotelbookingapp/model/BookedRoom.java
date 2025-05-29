package com.hotelbooking.hotelbookingapp.model;

import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private int count;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public BookedRoom() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id       The ID of the booked room record.
     * @param booking  The booking this room record belongs to.
     * @param roomType The type of the room booked.
     * @param count    The number of rooms of this type booked.
     */
    public BookedRoom(Long id, Booking booking, RoomType roomType, int count) {
        this.id = id;
        this.booking = booking;
        this.roomType = roomType;
        this.count = count;
    }

    /**
     * Constructor for essential fields, excluding id.
     *
     * @param booking  The booking this room record belongs to.
     * @param roomType The type of the room booked.
     * @param count    The number of rooms of this type booked.
     */
    public BookedRoom(Booking booking, RoomType roomType, int count) {
        this.booking = booking;
        this.roomType = roomType;
        this.count = count;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "BookedRoom{" +
               "id=" + id +
               ", bookingId=" + (booking != null ? booking.getId() : "null") + // Menampilkan bookingId
               ", roomType=" + roomType +
               ", count=" + count +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookedRoom that = (BookedRoom) o;
        // Untuk entitas JPA, kesetaraan seringkali didasarkan pada ID setelah persistensi.
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        // Jika ID null (entitas baru), bandingkan berdasarkan field lain yang mungkin membentuk kunci alami.
        // Dalam kasus ini, mungkin booking, roomType, dan count.
        // Namun, untuk kesederhanaan, jika ID tidak ada, kita bisa menganggapnya tidak sama kecuali instance yang sama.
        // Atau, jika ada business key yang jelas (misalnya, kombinasi booking & roomType unik), gunakan itu.
        // Implementasi saat ini hanya akan mengandalkan ID jika ada.
        return Objects.equals(id, that.id) && // Baris ini akan menyebabkan objek baru selalu tidak sama kecuali ID di-set
               Objects.equals(booking, that.booking) && // Baris ini bisa dipertimbangkan kembali
               roomType == that.roomType &&
               count == that.count;
        // Alternatif yang lebih sederhana jika fokus pada ID:
        // return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // Harus konsisten dengan equals.
        // Jika equals hanya berdasarkan id (setelah persistensi), hashCode juga harus demikian.
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(booking, roomType, count);
        // Alternatif yang lebih sederhana jika fokus pada ID:
        // return Objects.hash(id);
    }
}