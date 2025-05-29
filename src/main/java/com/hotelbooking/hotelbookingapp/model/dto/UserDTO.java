package com.hotelbooking.hotelbookingapp.model.dto;

import com.hotelbooking.hotelbookingapp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public class UserDTO {

    private Long id;

    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Invalid email address")
    private String username;

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Name must only contain letters")
    private String name;

    @NotBlank(message = "Last name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Last name must only contain letters")
    private String lastName;

    private Role role;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public UserDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public UserDTO(Long id, String username, String name, String lastName, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.role = role;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "UserDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", lastName='" + lastName + '\'' +
               ", role=" + (role != null ? role.getRoleType() : "null") + // Tampilkan roleType untuk info lebih
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
               Objects.equals(username, userDTO.username) &&
               Objects.equals(name, userDTO.name) &&
               Objects.equals(lastName, userDTO.lastName) &&
               Objects.equals(role, userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, lastName, role);
    }
}