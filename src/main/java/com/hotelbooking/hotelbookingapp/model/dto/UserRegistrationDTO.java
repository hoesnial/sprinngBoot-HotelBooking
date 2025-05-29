package com.hotelbooking.hotelbookingapp.model.dto;

import com.hotelbooking.hotelbookingapp.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserRegistrationDTO {

    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Invalid email address")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
    private String password;

    @NotBlank(message = "Name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Name must only contain letters")
    private String name;

    @NotBlank(message = "Last name cannot be empty")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Last name must only contain letters")
    private String lastName;

    private RoleType roleType;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public UserRegistrationDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public UserRegistrationDTO(String username, String password, String name, String lastName, RoleType roleType) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.roleType = roleType;
    }

    // --- Getters and Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        // Hindari mencetak password dalam log produksi
        return "UserRegistrationDTO{" +
               "username='" + username + '\'' +
               ", password='********'," + // Jangan sertakan password aktual
               ", name='" + name + '\'' +
               ", lastName='" + lastName + '\'' +
               ", roleType=" + roleType +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegistrationDTO that = (UserRegistrationDTO) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(password, that.password) &&
               Objects.equals(name, that.name) &&
               Objects.equals(lastName, that.lastName) &&
               roleType == that.roleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, name, lastName, roleType);
    }
}