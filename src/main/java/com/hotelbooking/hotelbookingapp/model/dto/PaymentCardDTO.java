package com.hotelbooking.hotelbookingapp.model.dto;

import com.hotelbooking.hotelbookingapp.validation.annotation.CardExpiry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.util.Objects;

public class PaymentCardDTO {

    @NotBlank(message = "Cardholder name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Cardholder name must contain only letters and spaces")
    @Size(min = 3, max = 50, message = "Cardholder name should be between 3 and 50 characters")
    private String cardholderName;

    @CreditCardNumber(message = "Invalid credit card number")
    private String cardNumber;

    @CardExpiry
    private String expirationDate;

    @Pattern(regexp = "^\\d{3}$", message = "CVC must be 3 digits")
    private String cvc;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public PaymentCardDTO() {
    }

    /**
     * Constructor with all arguments.
     */
    public PaymentCardDTO(String cardholderName, String cardNumber, String expirationDate, String cvc) {
        this.cardholderName = cardholderName;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvc = cvc;
    }

    // --- Getters and Setters ---

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        // Hindari mencetak detail kartu sensitif dalam log produksi
        return "PaymentCardDTO{" +
               "cardholderName='" + cardholderName + '\'' +
               // Jangan sertakan cardNumber, expirationDate, atau cvc dalam toString() untuk keamanan
               // ", cardNumber='****" + (cardNumber != null && cardNumber.length() > 4 ? cardNumber.substring(cardNumber.length() - 4) : "") + '\'' +
               // ", expirationDate='**/**'" +
               // ", cvc='***'" +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentCardDTO that = (PaymentCardDTO) o;
        return Objects.equals(cardholderName, that.cardholderName) &&
               Objects.equals(cardNumber, that.cardNumber) &&
               Objects.equals(expirationDate, that.expirationDate) &&
               Objects.equals(cvc, that.cvc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardholderName, cardNumber, expirationDate, cvc);
    }
}