package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Admin() {
    }

    /**
     * Constructor with all arguments.
     *
     * @param id   The admin's ID.
     * @param user The associated user.
     */
    public Admin(Long id, User user) {
        this.id = id;
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

    // --- Overridden Object methods ---

    @Override
    public String toString() {
        return "Admin{" +
               "id=" + id +
               ", userId=" + (user != null ? user.getId() : "null") + // Display user ID to avoid potential StackOverflowError
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        // For entities, equality is often best based on ID after persistence.
        // If IDs are null (before persistence), then potentially other business keys.
        // Here, both id and the associated user's equality are considered.
        return Objects.equals(id, admin.id);
    }

    @Override
    public int hashCode() {
        // Consistent with equals: if equals relies on 'id', hashCode should too.
        return Objects.hash(id);
    }
}