package com.hotelbooking.hotelbookingapp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp // Dikelola oleh Hibernate, biasanya tidak memerlukan setter manual dari aplikasi
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private HotelManager hotelManager;

    // Constructor Tanpa Argumen (menggantikan @NoArgsConstructor)
    public User() {
    }

    // Constructor Dengan Semua Argumen (menggantikan @AllArgsConstructor)
    // Perhatikan bahwa 'createdDate' biasanya diisi oleh @CreationTimestamp,
    // dan 'id' oleh @GeneratedValue. Anda mungkin ingin menyesuaikan constructor ini
    // jika beberapa field tidak seharusnya diatur secara manual saat pembuatan.
    // Namun, untuk konsistensi dengan @AllArgsConstructor, semua field disertakan di sini.
    public User(Long id, String username, String password, LocalDateTime createdDate, String name, String lastName, Role role, Admin admin, Customer customer, HotelManager hotelManager) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdDate = createdDate;
        this.name = name;
        this.lastName = lastName;
        this.role = role;
        this.admin = admin;
        this.customer = customer;
        this.hotelManager = hotelManager;
    }

    // Getters (menggantikan @Getter)
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

    public Admin getAdmin() {
        return admin;
    }

    public Customer getCustomer() {
        return customer;
    }

    public HotelManager getHotelManager() {
        return hotelManager;
    }

    // Setters (menggantikan @Setter)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Setter untuk createdDate biasanya tidak diperlukan jika menggunakan @CreationTimestamp,
    // karena Hibernate akan mengelolanya. Namun, jika Anda membutuhkannya:
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        // Jika relasi dua arah, Anda mungkin perlu mengatur sisi lainnya juga
        // if (admin != null && admin.getUser() != this) {
        //     admin.setUser(this);
        // }
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        // if (customer != null && customer.getUser() != this) {
        //     customer.setUser(this);
        // }
    }

    public void setHotelManager(HotelManager hotelManager) {
        this.hotelManager = hotelManager;
        // if (hotelManager != null && hotelManager.getUser() != this) {
        //     hotelManager.setUser(this);
        // }
    }

    // Method toString(), equals(), dan hashCode() sudah ada, jadi kita biarkan.
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", createdDate=" + createdDate +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + (role != null ? role.getId() : "null") + // Hindari System.out.println dari objek Role secara langsung jika bisa menyebabkan loop
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Pertimbangkan field mana yang benar-benar mendefinisikan kesetaraan unik.
        // Biasanya 'id' jika sudah ada, atau 'username' jika unik.
        return Objects.equals(id, user.id) ||
               (id == null && user.id == null && Objects.equals(username, user.username));
    }

    @Override
    public int hashCode() {
        // Konsisten dengan equals. Jika equals hanya berdasarkan id (setelah persistensi)
        // atau username (sebelum persistensi), hashCode juga harus demikian.
        return Objects.hash(id, username);
    }
}