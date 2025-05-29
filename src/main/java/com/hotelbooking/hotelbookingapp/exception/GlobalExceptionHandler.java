package com.hotelbooking.hotelbookingapp.exception;

import org.slf4j.Logger; // Pastikan impor ini ada
import org.slf4j.LoggerFactory; // Tambahkan impor untuk Logger
import org.springframework.web.bind.annotation.ControllerAdvice; // Tambahkan impor untuk LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice // Komentar dihilangkan, kelas ini sekarang aktif
public class GlobalExceptionHandler {

    // Tambahkan logger jika Anda ingin mencatat exception yang ditangani secara global
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException ditangani: {}", ex.getMessage()); // Contoh logging
        ModelAndView modelAndView = new ModelAndView("error"); // Nama view template untuk halaman error
        modelAndView.addObject("errorMessage", ex.getMessage()); // Pesan error yang akan ditampilkan
        return modelAndView;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException ditangani: {}", ex.getMessage()); // Contoh logging
        ModelAndView modelAndView = new ModelAndView("error"); // Nama view template untuk halaman error
        modelAndView.addObject("errorMessage", ex.getMessage()); // Pesan error yang akan ditampilkan
        return modelAndView;
    }

    // Anda bisa menambahkan handler untuk exception umum lainnya di sini jika perlu
    /*
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        log.error("Exception umum ditangani: {}", ex.getMessage(), ex); // Catat juga stack trace untuk exception umum
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Terjadi kesalahan yang tidak terduga. Silakan coba lagi nanti.");
        return modelAndView;
    }
    */
}