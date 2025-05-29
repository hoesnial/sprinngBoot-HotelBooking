package com.hotelbooking.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class ResetPasswordDTO {

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
    private String oldPassword;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
    private String newPassword;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
    private String confirmNewPassword;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public ResetPasswordDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public ResetPasswordDTO(String oldPassword, String newPassword, String confirmNewPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    // --- Getters and Setters ---

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        // Hindari mencetak password dalam log produksi
        return "ResetPasswordDTO{" +
               "oldPassword='********'," + // Jangan sertakan password aktual
               "newPassword='********'," + // Jangan sertakan password aktual
               "confirmNewPassword='********'" + // Jangan sertakan password aktual
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetPasswordDTO that = (ResetPasswordDTO) o;
        return Objects.equals(oldPassword, that.oldPassword) &&
               Objects.equals(newPassword, that.newPassword) &&
               Objects.equals(confirmNewPassword, that.confirmNewPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldPassword, newPassword, confirmNewPassword);
    }
}