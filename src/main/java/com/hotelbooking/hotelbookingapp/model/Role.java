package com.hotelbooking.hotelbookingapp.model;

import com.hotelbooking.hotelbookingapp.model.enums.RoleType;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleType roleType;

    // --- Constructors ---

    /**
     * Default constructor.
     * Diperlukan oleh JPA dan untuk menggantikan @NoArgsConstructor.
     */
    public Role() {
    }

    /**
     * Constructor untuk membuat Role baru dengan roleType tertentu.
     * Ini adalah constructor yang sudah ada sebelumnya.
     * @param roleType Tipe peran.
     */
    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    /**
     * Constructor dengan semua argumen (menggantikan @AllArgsConstructor).
     * Biasanya ID di-generate, jadi constructor ini mungkin jarang digunakan secara langsung.
     * @param id ID peran.
     * @param roleType Tipe peran.
     */
    public Role(Long id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    // --- Overridden Object methods ---
    // Metode toString(), equals(), dan hashCode() yang sudah ada dipertahankan.

    @Override
    public String toString() {
        return "Role{" +
               "id=" + id +
               ", roleType=" + roleType + // Mengubah 'name' menjadi 'roleType' agar sesuai dengan nama field
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        // Kesetaraan utama berdasarkan ID jika sudah ada (setelah persistensi)
        // Jika ID null, roleType bisa menjadi kandidat karena unik.
        if (id != null && role.id != null) {
            return Objects.equals(id, role.id);
        }
        // Jika ID null, bandingkan berdasarkan roleType (karena unik)
        return roleType == role.roleType;
    }

    @Override
    public int hashCode() {
        // Harus konsisten dengan equals.
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(roleType);
    }
}