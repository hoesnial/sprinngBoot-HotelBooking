package com.hotelbooking.hotelbookingapp.security;

import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.repository.UserRepository;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Manual constructor to replace @RequiredArgsConstructor
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    getAuthorities(user));
        } else {
            throw new UsernameNotFoundException("Invalid username or password!");
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleType().name()));
        return authorities;
    }

}