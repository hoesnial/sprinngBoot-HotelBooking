package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String confirmationNumber;

    @CreationTimestamp // Dikelola oleh Hibernate, biasanya tidak memerlukan setter manual dari aplikasi
    private LocalDateTime bookingDate;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = true) // Sesuai dengan definisi asli
    private Hotel hotel;

    @Column(nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false)
    private LocalDate checkoutDate;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookedRoom> bookedRooms = new ArrayList<>(); // Inisialisasi default dipertahankan

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Direkomendasikan fetch lazy dan cascade all + orphan removal jika payment dimiliki oleh booking
    private Payment payment;

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'bookedRooms' dengan ArrayList kosong.
     * confirmationNumber akan di-generate oleh @PrePersist.
     */
    public Booking() {
        // Inisialisasi 'bookedRooms' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     * Perhatikan bahwa confirmationNumber dan bookingDate biasanya di-generate secara otomatis.
     */
    public Booking(Long id, String confirmationNumber, LocalDateTime bookingDate, Customer customer, Hotel hotel,
                   LocalDate checkinDate, LocalDate checkoutDate, List<BookedRoom> bookedRooms, Payment payment) {
        this.id = id;
        this.confirmationNumber = confirmationNumber; // Mungkin akan di-overwrite oleh @PrePersist jika null
        this.bookingDate = bookingDate; // Mungkin akan di-overwrite oleh @CreationTimestamp jika null
        this.customer = customer;
        this.hotel = hotel;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        if (bookedRooms != null) {
            this.bookedRooms = bookedRooms;
        } else {
            this.bookedRooms = new ArrayList<>();
        }
        this.payment = payment;
    }

    /**
     * Constructor for essential fields, common for new bookings.
     */
     public Booking(Customer customer, Hotel hotel, LocalDate checkinDate, LocalDate checkoutDate) {
        this.customer = customer;
        this.hotel = hotel;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        // confirmationNumber akan di-generate by @PrePersist
        // bookingDate akan di-generate by @CreationTimestamp
        // bookedRooms diinisialisasi sebagai list kosong
     }


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Setter untuk confirmationNumber biasanya tidak dipanggil secara manual
     * karena di-generate oleh @PrePersist.
     */
    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    /**
     * Setter untuk bookingDate biasanya tidak dipanggil secara manual
     * karena di-generate oleh @CreationTimestamp.
     */
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
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

    public List<BookedRoom> getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(List<BookedRoom> bookedRooms) {
        this.bookedRooms = bookedRooms;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        // Jika relasi dua arah, pastikan sisi lain juga di-set
        // if (payment != null && payment.getBooking() != this) {
        //     payment.setBooking(this);
        // }
    }

    // --- Helper methods for managing bookedRooms (opsional tapi direkomendasikan) ---
    public void addBookedRoom(BookedRoom bookedRoom) {
        if (bookedRoom != null) {
            this.bookedRooms.add(bookedRoom);
            bookedRoom.setBooking(this); // Menjaga konsistensi dua arah
        }
    }

    public void removeBookedRoom(BookedRoom bookedRoom) {
        if (bookedRoom != null) {
            this.bookedRooms.remove(bookedRoom);
            bookedRoom.setBooking(null); // Menjaga konsistensi dua arah
        }
    }


    // --- JPA Lifecycle Callbacks ---
    @PrePersist
    protected void onCreate() {
        if (this.confirmationNumber == null || this.confirmationNumber.trim().isEmpty()) {
            this.confirmationNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        }
    }

    // --- Overridden Object methods ---
    @Override
    public String toString() {
        return "Booking{" +
               "id=" + id +
               ", confirmationNumber='" + confirmationNumber + '\'' +
               ", bookingDate=" + bookingDate +
               ", customerId=" + (customer != null ? customer.getId() : "null") +
               ", hotelId=" + (hotel != null ? hotel.getId() : "null") +
               ", checkinDate=" + checkinDate +
               ", checkoutDate=" + checkoutDate +
               ", numberOfBookedRooms=" + (bookedRooms != null ? bookedRooms.size() : 0) +
               ", paymentId=" + (payment != null ? payment.getId() : "null") +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        // Kesetaraan utama berdasarkan ID jika sudah ada (setelah persistensi)
        if (id != null && booking.id != null) {
            return Objects.equals(id, booking.id);
        }
        // Jika ID null (objek baru), confirmationNumber bisa menjadi kandidat,
        // meskipun itu di-generate @PrePersist jadi mungkin tidak ideal untuk perbandingan objek baru
        return Objects.equals(confirmationNumber, booking.confirmationNumber);
    }

    @Override
    public int hashCode() {
        // Konsisten dengan equals
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(confirmationNumber);
    }
}