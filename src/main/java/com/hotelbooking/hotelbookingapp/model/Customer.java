package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true) // Ditambahkan cascade dan orphanRemoval untuk pengelolaan bookingList yang lebih baik
    private List<Booking> bookingList = new ArrayList<>();

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Customer() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id          The customer's ID.
     * @param user        The associated user.
     * @param bookingList The list of bookings for this customer.
     */
    public Customer(Long id, User user, List<Booking> bookingList) {
        this.id = id;
        this.user = user;
        if (bookingList != null) { // Menghindari NullPointerException jika bookingList null
            this.bookingList = bookingList;
        }
    }

    // Constructor alternatif yang lebih umum digunakan (tanpa bookingList saat pembuatan awal)
    public Customer(User user) {
        this.user = user;
    }


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    // --- Helper methods for managing bookingList (opsional tapi direkomendasikan) ---
    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookingList.add(booking);
            booking.setCustomer(this); // Menjaga konsistensi dua arah
        }
    }

    public void removeBooking(Booking booking) {
        if (booking != null) {
            this.bookingList.remove(booking);
            booking.setCustomer(null); // Menjaga konsistensi dua arah
        }
    }

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", userId=" + (user != null ? user.getId() : "null") + // Menampilkan userId untuk menghindari StackOverflowError
               ", numberOfBookings=" + (bookingList != null ? bookingList.size() : 0) +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        // Untuk entitas JPA, kesetaraan seringkali didasarkan pada ID setelah persistensi.
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        // Konsisten dengan equals. Jika equals hanya berdasarkan 'id', hashCode juga harus demikian.
        return Objects.hash(id);
    }
}