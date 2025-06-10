package com.hotelbooking.hotelbookingapp.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotelbooking.hotelbookingapp.model.dto.BookingDTO;
import com.hotelbooking.hotelbookingapp.model.dto.BookingInitiationDTO;
import com.hotelbooking.hotelbookingapp.model.dto.HotelDTO;
import com.hotelbooking.hotelbookingapp.model.dto.PaymentCardDTO;
import com.hotelbooking.hotelbookingapp.model.dto.UserDTO;
import com.hotelbooking.hotelbookingapp.service.BookingService;
import com.hotelbooking.hotelbookingapp.service.HotelService;
import com.hotelbooking.hotelbookingapp.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;

    // Constructor manual untuk menggantikan @RequiredArgsConstructor
    public BookingController(HotelService hotelService, UserService userService, BookingService bookingService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping("/initiate")
    public String initiateBooking(@ModelAttribute BookingInitiationDTO bookingInitiationDTO, HttpSession session) {
        session.setAttribute("bookingInitiationDTO", bookingInitiationDTO);
        log.debug("BookingInitiationDTO set in session: {}", bookingInitiationDTO);
        return "redirect:/booking/payment";
    }

    @GetMapping("/payment")
    public String showPaymentPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        BookingInitiationDTO bookingInitiationDTO = (BookingInitiationDTO) session.getAttribute("bookingInitiationDTO");
        log.debug("BookingInitiationDTO retrieved from session: {}", bookingInitiationDTO);

        if (bookingInitiationDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your session has expired. Please start a new search.");
            return "redirect:/search";
        }

        HotelDTO hotelDTO = hotelService.findHotelDtoById(bookingInitiationDTO.getHotelId());

        model.addAttribute("bookingInitiationDTO", bookingInitiationDTO);
        model.addAttribute("hotelDTO", hotelDTO);
        model.addAttribute("paymentCardDTO", new PaymentCardDTO());

        return "booking/payment";
    }

    @PostMapping("/payment")
    public String confirmBooking(@Valid @ModelAttribute("paymentCardDTO") PaymentCardDTO paymentDTO, BindingResult result, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        BookingInitiationDTO bookingInitiationDTO = (BookingInitiationDTO) session.getAttribute("bookingInitiationDTO");
        log.debug("BookingInitiationDTO retrieved from session at the beginning of completeBooking: {}", bookingInitiationDTO);
        if (bookingInitiationDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your session has expired. Please start a new search.");
            return "redirect:/search";
        }

        if (result.hasErrors()) {
            log.warn("Validation errors occurred while completing booking");
            HotelDTO hotelDTO = hotelService.findHotelDtoById(bookingInitiationDTO.getHotelId());
            model.addAttribute("bookingInitiationDTO", bookingInitiationDTO);
            model.addAttribute("hotelDTO", hotelDTO);
            model.addAttribute("paymentCardDTO", paymentDTO); // Menggunakan paymentDTO yang sudah divalidasi
            return "booking/payment";
        }

        try {
            Long userId = getLoggedInUserId();
            BookingDTO bookingDTO = bookingService.confirmBooking(bookingInitiationDTO, userId);
            redirectAttributes.addFlashAttribute("bookingDTO", bookingDTO);

            return "redirect:/booking/confirmation";
        } catch (Exception e) {
            log.error("An error occurred while completing the booking", e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            return "redirect:/booking/payment";
        }
    }

    @GetMapping("/confirmation")
    public String showConfirmationPage(Model model, RedirectAttributes redirectAttributes) {
        // Attempt to retrieve the bookingDTO from flash attributes
        BookingDTO bookingDTO = (BookingDTO) model.asMap().get("bookingDTO");

        if (bookingDTO == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your session has expired or the booking process was not completed properly. Please start a new search.");
            return "redirect:/search";
        }

        LocalDate checkinDate = bookingDTO.getCheckinDate();
        LocalDate checkoutDate = bookingDTO.getCheckoutDate();
        long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        model.addAttribute("days", durationDays);
        model.addAttribute("bookingDTO", bookingDTO); // bookingDTO sudah ada di model dari flash attribute

        return "booking/confirmation";
    }

    private Long getLoggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.findUserDTOByUsername(username);
        log.info("Fetched logged in user ID: {}", userDTO.getId());
        return userDTO.getId();
    }

}