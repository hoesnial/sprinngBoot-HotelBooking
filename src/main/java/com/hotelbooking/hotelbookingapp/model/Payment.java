package com.hotelbooking.hotelbookingapp.model;

import com.hotelbooking.hotelbookingapp.model.enums.Currency;
import com.hotelbooking.hotelbookingapp.model.enums.PaymentMethod;
import com.hotelbooking.hotelbookingapp.model.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @CreationTimestamp // Dikelola oleh Hibernate, biasanya tidak memerlukan setter manual dari aplikasi
    private LocalDateTime paymentDate;

    @OneToOne(fetch = FetchType.LAZY) // Direkomendasikan fetch = FetchType.LAZY
    @JoinColumn(nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    // --- Constructors ---

    /**
     * Default constructor.
     * transactionId akan di-generate oleh @PrePersist.
     * paymentDate akan di-generate oleh @CreationTimestamp.
     */
    public Payment() {
    }

    /**
     * Constructor with all arguments.
     * Perhatikan bahwa transactionId dan paymentDate biasanya di-generate secara otomatis.
     */
    public Payment(Long id, String transactionId, LocalDateTime paymentDate, Booking booking, BigDecimal totalPrice,
                   PaymentStatus paymentStatus, PaymentMethod paymentMethod, Currency currency) {
        this.id = id;
        this.transactionId = transactionId; // Mungkin akan di-overwrite oleh @PrePersist jika null
        this.paymentDate = paymentDate; // Mungkin akan di-overwrite oleh @CreationTimestamp jika null
        this.booking = booking;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
    }

    /**
     * Constructor for essential fields, common for new payments.
     */
    public Payment(Booking booking, BigDecimal totalPrice, PaymentStatus paymentStatus, PaymentMethod paymentMethod, Currency currency) {
        this.booking = booking;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        // transactionId akan di-generate oleh @PrePersist
        // paymentDate akan di-generate oleh @CreationTimestamp
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Setter untuk transactionId biasanya tidak dipanggil secara manual
     * karena di-generate oleh @PrePersist.
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Setter untuk paymentDate biasanya tidak dipanggil secara manual
     * karena di-generate oleh @CreationTimestamp.
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    // --- JPA Lifecycle Callbacks ---
    @PrePersist
    protected void onCreate() {
        if (this.transactionId == null || this.transactionId.trim().isEmpty()) {
            this.transactionId = UUID.randomUUID().toString();
        }
    }

    // --- Overridden Object methods ---
    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", transactionId='" + transactionId + '\'' +
               ", paymentDate=" + paymentDate +
               ", bookingId=" + (booking != null ? booking.getId() : "null") + // Menampilkan bookingId
               ", totalPrice=" + totalPrice +
               ", paymentStatus=" + paymentStatus +
               ", paymentMethod=" + paymentMethod +
               ", currency=" + currency +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        // Kesetaraan utama berdasarkan ID jika sudah ada (setelah persistensi)
        if (id != null && payment.id != null) {
            return Objects.equals(id, payment.id);
        }
        // Jika ID null (objek baru), transactionId bisa menjadi kandidat,
        // meskipun itu di-generate @PrePersist jadi mungkin tidak ideal untuk perbandingan objek baru
        return Objects.equals(transactionId, payment.transactionId);
    }

    @Override
    public int hashCode() {
        // Konsisten dengan equals
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(transactionId);
    }
}