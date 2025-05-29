package com.hotelbooking.hotelbookingapp.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class RedirectUtil {

    public static String getRedirectUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            // Menggunakan enhanced switch (rule switch)
            switch (authority) {
                case "ROLE_ADMIN" -> {
                    return "/admin/dashboard";
                }
                case "ROLE_CUSTOMER" -> {
                    return "/search"; // Atau "/customer/dashboard" jika lebih sesuai
                }
                case "ROLE_HOTEL_MANAGER" -> {
                    return "/manager/dashboard";
                }
                // Tidak ada 'default' di sini karena kita ingin melanjutkan loop
                // jika authority saat ini tidak cocok dengan case di atas.
            }
        }
        return null; // Kembalikan null jika tidak ada role yang cocok setelah memeriksa semua authorities
    }

}