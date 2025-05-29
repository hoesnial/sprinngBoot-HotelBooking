package com.hotelbooking.hotelbookingapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class HotelManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "hotelManager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hotelList = new ArrayList<>();

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public HotelManager() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id        The hotel manager's ID.
     * @param user      The associated user.
     * @param hotelList The list of hotels managed by this manager.
     */
    public HotelManager(Long id, User user, List<Hotel> hotelList) {
        this.id = id;
        this.user = user;
        if (hotelList != null) { // Avoid NullPointerException
            this.hotelList = hotelList;
        }
    }

    /**
     * Constructor primarily for associating a user.
     * hotelList is initialized as empty.
     * @param user The associated user.
     */
    public HotelManager(User user) {
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

    public List<Hotel> getHotelList() {
        return hotelList;
    }

    public void setHotelList(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    // --- Helper methods for managing hotelList (optional but recommended) ---
    public void addHotel(Hotel hotel) {
        if (hotel != null) {
            this.hotelList.add(hotel);
            hotel.setHotelManager(this); // Maintain bidirectional consistency
        }
    }

    public void removeHotel(Hotel hotel) {
        if (hotel != null) {
            this.hotelList.remove(hotel);
            hotel.setHotelManager(null); // Maintain bidirectional consistency
        }
    }

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "HotelManager{" +
               "id=" + id +
               ", userId=" + (user != null ? user.getId() : "null") + // Display user ID
               ", numberOfHotelsManaged=" + (hotelList != null ? hotelList.size() : 0) +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelManager that = (HotelManager) o;
        // For JPA entities, equality is often best based on ID after persistence.
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // Consistent with equals: if equals relies on 'id', hashCode should too.
        return Objects.hash(id);
    }
}