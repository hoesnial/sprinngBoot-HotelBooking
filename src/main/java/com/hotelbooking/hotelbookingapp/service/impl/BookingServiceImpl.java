package com.hotelbooking.hotelbookingapp.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hotelbooking.hotelbookingapp.model.BookedRoom;
import com.hotelbooking.hotelbookingapp.model.Booking;
import com.hotelbooking.hotelbookingapp.model.Customer;
import com.hotelbooking.hotelbookingapp.model.Hotel;
import com.hotelbooking.hotelbookingapp.model.Payment;
import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.model.dto.AddressDTO;
import com.hotelbooking.hotelbookingapp.model.dto.BookingDTO;
import com.hotelbooking.hotelbookingapp.model.dto.BookingInitiationDTO;
import com.hotelbooking.hotelbookingapp.model.dto.RoomSelectionDTO;
import com.hotelbooking.hotelbookingapp.repository.BookingRepository;
import com.hotelbooking.hotelbookingapp.service.AvailabilityService;
import com.hotelbooking.hotelbookingapp.service.BookingService;
import com.hotelbooking.hotelbookingapp.service.CustomerService;
import com.hotelbooking.hotelbookingapp.service.HotelService;
import com.hotelbooking.hotelbookingapp.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class BookingServiceImpl implements BookingService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final HotelService hotelService;

    // Manual constructor to replace @RequiredArgsConstructor
    public BookingServiceImpl(BookingRepository bookingRepository,
                              AvailabilityService availabilityService,
                              PaymentService paymentService,
                              CustomerService customerService,
                              HotelService hotelService) {
        this.bookingRepository = bookingRepository;
        this.availabilityService = availabilityService;
        this.paymentService = paymentService;
        this.customerService = customerService;
        this.hotelService = hotelService;
    }


    @Override
    @Transactional
    public Booking saveBooking(BookingInitiationDTO bookingInitiationDTO, Long userId) {
        log.debug("Attempting to save booking for user ID: {} and hotel ID: {}", userId, bookingInitiationDTO.getHotelId());
        validateBookingDates(bookingInitiationDTO.getCheckinDate(), bookingInitiationDTO.getCheckoutDate());

        Customer customer = customerService.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with user ID: " + userId));

        Hotel hotel = hotelService.findHotelById(bookingInitiationDTO.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + bookingInitiationDTO.getHotelId()));

        Booking booking = mapBookingInitDtoToBookingModel(bookingInitiationDTO, customer, hotel);
        log.info("Mapped BookingInitiationDTO to Booking model for saving");

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Successfully saved new booking with ID: {}", savedBooking.getId());
        return savedBooking;
    }

    @Override
    @Transactional
    public BookingDTO confirmBooking(BookingInitiationDTO bookingInitiationDTO, Long customerId) {
        log.info("Confirming booking for customer ID: {} and hotel ID: {}", customerId, bookingInitiationDTO.getHotelId());
        Booking savedBooking = this.saveBooking(bookingInitiationDTO, customerId);
        log.debug("Booking saved with ID: {}. Proceeding to save payment.", savedBooking.getId());

        Payment savedPayment = paymentService.savePayment(bookingInitiationDTO, savedBooking);
        log.debug("Payment saved for booking ID: {}. Setting payment to booking.", savedBooking.getId());
        savedBooking.setPayment(savedPayment);

        // Save booking again to establish the bidirectional link with Payment if necessary,
        // or if Payment side is the owner of the relationship and cascade is not set from Booking to Payment for merges.
        // Often, if Payment has a booking_id foreign key and is saved after Booking, this explicit save might not be needed
        // if the transaction handles the flush order correctly or if Payment is cascaded from Booking.
        // However, explicitly saving can ensure the state is persisted.
        bookingRepository.save(savedBooking);
        log.debug("Booking updated with payment information for booking ID: {}", savedBooking.getId());

        log.info("Updating availabilities for hotel ID: {} from {} to {}", bookingInitiationDTO.getHotelId(), bookingInitiationDTO.getCheckinDate(), bookingInitiationDTO.getCheckoutDate());
        availabilityService.updateAvailabilities(bookingInitiationDTO.getHotelId(), bookingInitiationDTO.getCheckinDate(),
                bookingInitiationDTO.getCheckoutDate(), bookingInitiationDTO.getRoomSelections());
        log.info("Availabilities updated. Mapping booking model to DTO for booking ID: {}", savedBooking.getId());
        return mapBookingModelToBookingDto(savedBooking);
    }

    @Override
    public List<BookingDTO> findAllBookings() {
        log.debug("Fetching all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapBookingModelToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingById(Long bookingId) {
        log.debug("Fetching booking by ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with ID: {}", bookingId);
                    return new EntityNotFoundException("Booking not found with ID: " + bookingId);
                });
        return mapBookingModelToBookingDto(booking);
    }

    @Override
    public List<BookingDTO> findBookingsByCustomerId(Long customerId) {
        log.debug("Fetching bookings for customer ID: {}", customerId);
        List<Booking> bookings = bookingRepository.findBookingsByCustomerId(customerId); // Corrected variable name
        return bookings.stream() // Use the correct variable
                .map(this::mapBookingModelToBookingDto)
                .sorted(Comparator.comparing(BookingDTO::getCheckinDate))
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingByIdAndCustomerId(Long bookingId, Long customerId) {
        log.debug("Fetching booking by ID: {} for customer ID: {}", bookingId, customerId);
        Booking booking = bookingRepository.findBookingByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with ID: {} for customer ID: {}", bookingId, customerId);
                    return new EntityNotFoundException("Booking not found with ID: " + bookingId + " for customer ID: " + customerId);
                });
        return mapBookingModelToBookingDto(booking);
    }

    @Override
    public List<BookingDTO> findBookingsByManagerId(Long managerId) {
        log.debug("Fetching bookings for manager ID: {}", managerId);
        List<Hotel> hotels = hotelService.findAllHotelsByManagerId(managerId);
        if (hotels.isEmpty()) {
            log.info("No hotels found for manager ID: {}. Returning empty booking list.", managerId);
            return new ArrayList<>();
        }
        return hotels.stream()
                .flatMap(hotel -> {
                    log.trace("Fetching bookings for hotel ID: {}", hotel.getId());
                    return bookingRepository.findBookingsByHotelId(hotel.getId()).stream();
                })
                .map(this::mapBookingModelToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO findBookingByIdAndManagerId(Long bookingId, Long managerId) {
        log.debug("Fetching booking by ID: {} for manager ID: {}", bookingId, managerId);
        Booking booking = bookingRepository.findBookingByIdAndHotel_HotelManagerId(bookingId, managerId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with ID: {} for manager ID: {}", bookingId, managerId);
                    return new EntityNotFoundException("Booking not found with ID: " + bookingId + " and manager ID: " + managerId);
                });
        return mapBookingModelToBookingDto(booking);
    }

    private void validateBookingDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate == null || checkoutDate == null) {
            throw new IllegalArgumentException("Check-in and Check-out dates cannot be null");
        }
        if (checkinDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        if (!checkoutDate.isAfter(checkinDate)) { // Corrected logic: checkout must be strictly after checkin
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    @Override
    public BookingDTO mapBookingModelToBookingDto(Booking booking) {
        // Replaced .builder() with constructor and setters for AddressDTO
        AddressDTO addressDto = new AddressDTO();
        if (booking.getHotel() != null && booking.getHotel().getAddress() != null) {
            addressDto.setAddressLine(booking.getHotel().getAddress().getAddressLine());
            addressDto.setCity(booking.getHotel().getAddress().getCity());
            addressDto.setCountry(booking.getHotel().getAddress().getCountry());
        }

        List<RoomSelectionDTO> roomSelections = booking.getBookedRooms().stream()
                .map(room -> {
                    // Replaced .builder() with constructor and setters for RoomSelectionDTO
                    RoomSelectionDTO selectionDTO = new RoomSelectionDTO();
                    selectionDTO.setRoomType(room.getRoomType());
                    selectionDTO.setCount(room.getCount());
                    return selectionDTO;
                })
                .collect(Collectors.toList());

        User customerUser = booking.getCustomer().getUser();

        // Replaced .builder() with constructor and setters for BookingDTO
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setConfirmationNumber(booking.getConfirmationNumber());
        bookingDTO.setBookingDate(booking.getBookingDate());
        bookingDTO.setCustomerId(booking.getCustomer().getId());
        if (booking.getHotel() != null) {
            bookingDTO.setHotelId(booking.getHotel().getId());
            bookingDTO.setHotelName(booking.getHotel().getName());
        }
        bookingDTO.setCheckinDate(booking.getCheckinDate());
        bookingDTO.setCheckoutDate(booking.getCheckoutDate());
        bookingDTO.setRoomSelections(roomSelections);
        if (booking.getPayment() != null) {
            bookingDTO.setTotalPrice(booking.getPayment().getTotalPrice());
            bookingDTO.setPaymentStatus(booking.getPayment().getPaymentStatus());
            bookingDTO.setPaymentMethod(booking.getPayment().getPaymentMethod());
        }
        bookingDTO.setHotelAddress(addressDto);
        if (customerUser != null) {
            bookingDTO.setCustomerName(customerUser.getName() + " " + customerUser.getLastName());
            bookingDTO.setCustomerEmail(customerUser.getUsername());
        }

        return bookingDTO;
    }

    private Booking mapBookingInitDtoToBookingModel(BookingInitiationDTO bookingInitiationDTO, Customer customer, Hotel hotel) {
        // Replaced .builder() with constructor and setters for Booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setCheckinDate(bookingInitiationDTO.getCheckinDate());
        booking.setCheckoutDate(bookingInitiationDTO.getCheckoutDate());
        // bookedRooms will be initialized as an empty list in the Booking constructor or field declaration

        if (bookingInitiationDTO.getRoomSelections() != null) {
            for (RoomSelectionDTO roomSelection : bookingInitiationDTO.getRoomSelections()) {
                if (roomSelection.getCount() > 0) {
                    // Replaced .builder() with constructor and setters for BookedRoom
                    BookedRoom bookedRoom = new BookedRoom();
                    // booking.addBookedRoom(bookedRoom) will also set bookedRoom.setBooking(booking) if implemented
                    bookedRoom.setBooking(booking); // Ensure this link is set
                    bookedRoom.setRoomType(roomSelection.getRoomType());
                    bookedRoom.setCount(roomSelection.getCount());
                    booking.getBookedRooms().add(bookedRoom); // Or use a helper method like booking.addBookedRoom(bookedRoom)
                }
            }
        }
        return booking;
    }
}