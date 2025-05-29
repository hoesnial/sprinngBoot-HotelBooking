package com.hotelbooking.hotelbookingapp.controller;

import com.hotelbooking.hotelbookingapp.exception.HotelAlreadyExistsException;
import com.hotelbooking.hotelbookingapp.model.dto.BookingDTO;
import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import com.hotelbooking.hotelbookingapp.model.dto.HotelDTO;
import com.hotelbooking.hotelbookingapp.model.dto.HotelRegistrationDTO;
import com.hotelbooking.hotelbookingapp.model.dto.RoomDTO;
import com.hotelbooking.hotelbookingapp.service.BookingService;
import com.hotelbooking.hotelbookingapp.service.HotelService;
import com.hotelbooking.hotelbookingapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class HotelManagerController {

    private static final Logger log = LoggerFactory.getLogger(HotelManagerController.class);

    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;

    // Constructor manual untuk menggantikan @RequiredArgsConstructor
    public HotelManagerController(HotelService hotelService, UserService userService, BookingService bookingService) {
        this.hotelService = hotelService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "hotelmanager/dashboard";
    }

    @GetMapping("/hotels/add")
    public String showAddHotelForm(Model model) {
        HotelRegistrationDTO hotelRegistrationDTO = new HotelRegistrationDTO();

        // Initialize roomDTOs with SINGLE and DOUBLE room types
        // Menggunakan constructor RoomDTO yang sudah diubah tanpa Lombok
        RoomDTO singleRoom = new RoomDTO(null, null, RoomType.SINGLE, 0, 0.0);
        RoomDTO doubleRoom = new RoomDTO(null, null, RoomType.DOUBLE, 0, 0.0);
        hotelRegistrationDTO.getRoomDTOs().add(singleRoom);
        hotelRegistrationDTO.getRoomDTOs().add(doubleRoom);

        model.addAttribute("hotel", hotelRegistrationDTO);
        return "hotelmanager/hotels-add";
    }

    @PostMapping("/hotels/add")
    public String addHotel(@Valid @ModelAttribute("hotel") HotelRegistrationDTO hotelRegistrationDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("Hotel creation failed due to validation errors: {}", result.getAllErrors());
            return "hotelmanager/hotels-add";
        }
        try {
            hotelService.saveHotel(hotelRegistrationDTO);
            redirectAttributes.addFlashAttribute("message", "Hotel (" + hotelRegistrationDTO.getName() + ") added successfully");
            return "redirect:/manager/hotels";
        } catch (HotelAlreadyExistsException e) {
            result.rejectValue("name", "hotel.exists", e.getMessage());
            return "hotelmanager/hotels-add";
        }
    }

    @GetMapping("/hotels")
    public String listHotels(Model model) {
        Long managerId = getCurrentManagerId();
        List<HotelDTO> hotelList = hotelService.findAllHotelDtosByManagerId(managerId);
        model.addAttribute("hotels", hotelList);
        return "hotelmanager/hotels";
    }

    @GetMapping("/hotels/edit/{id}")
    public String showEditHotelForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            HotelDTO hotelDTO = hotelService.findHotelByIdAndManagerId(id, managerId);
            model.addAttribute("hotel", hotelDTO);
            return "hotelmanager/hotels-edit";
        } catch (EntityNotFoundException e) {
            log.error("Hotel not found with id {} for manager", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hotel not found or you do not have permission to edit it.");
            return "redirect:/manager/hotels";
        }
    }

    @PostMapping("/hotels/edit/{id}")
    public String editHotel(@PathVariable Long id, @Valid @ModelAttribute("hotel") HotelDTO hotelDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "hotelmanager/hotels-edit";
        }
        try {
            Long managerId = getCurrentManagerId();
            hotelDTO.setId(id); // Pastikan ID di set untuk update
            hotelService.updateHotelByManagerId(hotelDTO, managerId);
            redirectAttributes.addFlashAttribute("message", "Hotel (ID: " + id + ") updated successfully");
            return "redirect:/manager/hotels";

        } catch (HotelAlreadyExistsException e) {
            result.rejectValue("name", "hotel.exists", e.getMessage());
            return "hotelmanager/hotels-edit";
        } catch (EntityNotFoundException e) {
            // Seharusnya tidak terjadi jika showEditHotelForm berhasil, tapi sebagai fallback
            log.error("Error updating hotel, hotel not found with id {} for manager", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hotel not found or you do not have permission to edit it.");
            return "redirect:/manager/hotels";
        }
    }

    @PostMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            hotelService.deleteHotelByIdAndManagerId(id, managerId);
            redirectAttributes.addFlashAttribute("message", "Hotel (ID: " + id + ") deleted successfully.");
        } catch (EntityNotFoundException e) {
            log.error("Error deleting hotel, hotel not found with id {} for manager", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Hotel not found or you do not have permission to delete it.");
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting hotel with id {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while deleting the hotel.");
        }
        return "redirect:/manager/hotels";
    }

    @GetMapping("/bookings")
    public String listBookings(Model model, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            List<BookingDTO> bookingDTOs = bookingService.findBookingsByManagerId(managerId);
            model.addAttribute("bookings", bookingDTOs);
            return "hotelmanager/bookings";
        } catch (EntityNotFoundException e) { // Jika manager tidak ditemukan saat mengambil managerId
            log.error("No manager found, cannot list bookings", e);
            redirectAttributes.addFlashAttribute("errorMessage", "User profile not found. Please log in again.");
            return "redirect:/login"; // Atau ke halaman error yang sesuai
        } catch (Exception e) {
            log.error("An error occurred while listing bookings for manager", e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            return "redirect:/manager/dashboard";
        }
    }

    @GetMapping("/bookings/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            BookingDTO bookingDTO = bookingService.findBookingByIdAndManagerId(id, managerId);
            model.addAttribute("bookingDTO", bookingDTO);

            LocalDate checkinDate = bookingDTO.getCheckinDate();
            LocalDate checkoutDate = bookingDTO.getCheckoutDate();
            long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            model.addAttribute("days", durationDays);

            return "hotelmanager/bookings-details";
        } catch (EntityNotFoundException e) { // Bisa jadi manager tidak ditemukan atau booking tidak ditemukan/bukan milik manager
            log.error("Error viewing booking details for booking ID {} or manager not found", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found or you do not have permission to view it.");
            return "redirect:/manager/bookings"; // Kembali ke daftar booking manager
        } catch (Exception e) {
            log.error("An error occurred while displaying booking details for booking ID {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            return "redirect:/manager/dashboard";
        }
    }

    private Long getCurrentManagerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Panggil service untuk mendapatkan manager ID berdasarkan username.
        // Pastikan implementasi userService.findUserByUsername(username).getHotelManager().getId()
        // menangani kasus jika User atau HotelManager tidak ditemukan dengan baik (misalnya, melempar EntityNotFoundException).
        return userService.findUserByUsername(username).getHotelManager().getId();
    }
}