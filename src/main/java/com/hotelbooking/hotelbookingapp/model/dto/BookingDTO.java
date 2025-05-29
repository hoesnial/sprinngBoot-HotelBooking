package com.hotelbooking.hotelbookingapp.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hotelbooking.hotelbookingapp.model.enums.PaymentMethod;
import com.hotelbooking.hotelbookingapp.model.enums.PaymentStatus;

public class BookingDTO {

    private Long id;
    private String confirmationNumber;
    private LocalDateTime bookingDate;
    private Long customerId;
    private Long hotelId;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private List<RoomSelectionDTO> roomSelections = new ArrayList<>(); // Inisialisasi dipertahankan
    private BigDecimal totalPrice;
    private String hotelName;
    private AddressDTO hotelAddress;
    private String customerName;
    private String customerEmail;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    // --- Constructors ---

    /**
     * Default constructor.
     * Menginisialisasi 'roomSelections' dengan ArrayList kosong.
     */
    public BookingDTO() {
        // Inisialisasi 'roomSelections' sudah dilakukan di deklarasi field.
    }

    /**
     * Constructor with all arguments.
     */
    public BookingDTO(Long id, String confirmationNumber, LocalDateTime bookingDate, Long customerId, Long hotelId,
                      LocalDate checkinDate, LocalDate checkoutDate, List<RoomSelectionDTO> roomSelections,
                      BigDecimal totalPrice, String hotelName, AddressDTO hotelAddress, String customerName,
                      String customerEmail, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        this.id = id;
        this.confirmationNumber = confirmationNumber;
        this.bookingDate = bookingDate;
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        if (roomSelections != null) { // Menghindari NullPointerException
            this.roomSelections = roomSelections;
        } else {
            this.roomSelections = new ArrayList<>(); // Pastikan list selalu terinisialisasi
        }
        this.totalPrice = totalPrice;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public List<RoomSelectionDTO> getRoomSelections() {
        return roomSelections;
    }

    public void setRoomSelections(List<RoomSelectionDTO> roomSelections) {
        this.roomSelections = roomSelections;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public AddressDTO getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(AddressDTO hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    // --- Overridden Object methods (from @Data) ---

    @Override
    public String toString() {
        return "BookingDTO{" +
               "id=" + id +
               ", confirmationNumber='" + confirmationNumber + '\'' +
               ", bookingDate=" + bookingDate +
               ", customerId=" + customerId +
               ", hotelId=" + hotelId +
               ", checkinDate=" + checkinDate +
               ", checkoutDate=" + checkoutDate +
               ", roomSelections=" + (roomSelections != null ? roomSelections.size() : 0) + // Tampilkan ukuran untuk keringkasan
               ", totalPrice=" + totalPrice +
               ", hotelName='" + hotelName + '\'' +
               ", hotelAddress=" + (hotelAddress != null ? hotelAddress.getCity() : "null") + // Contoh: tampilkan kota dari alamat
               ", customerName='" + customerName + '\'' +
               ", customerEmail='" + customerEmail + '\'' +
               ", paymentStatus=" + paymentStatus +
               ", paymentMethod=" + paymentMethod +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDTO that = (BookingDTO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(confirmationNumber, that.confirmationNumber) &&
               Objects.equals(bookingDate, that.bookingDate) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(hotelId, that.hotelId) &&
               Objects.equals(checkinDate, that.checkinDate) &&
               Objects.equals(checkoutDate, that.checkoutDate) &&
               Objects.equals(roomSelections, that.roomSelections) &&
               Objects.equals(totalPrice, that.totalPrice) &&
               Objects.equals(hotelName, that.hotelName) &&
               Objects.equals(hotelAddress, that.hotelAddress) &&
               Objects.equals(customerName, that.customerName) &&
               Objects.equals(customerEmail, that.customerEmail) &&
               paymentStatus == that.paymentStatus &&
               paymentMethod == that.paymentMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, confirmationNumber, bookingDate, customerId, hotelId, checkinDate, checkoutDate,
                            roomSelections, totalPrice, hotelName, hotelAddress, customerName, customerEmail,
                            paymentStatus, paymentMethod);
    }
}