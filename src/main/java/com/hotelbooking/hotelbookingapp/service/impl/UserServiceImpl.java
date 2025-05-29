package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.Customer;
import com.hotelbooking.hotelbookingapp.model.Role;
import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.model.HotelManager;
import com.hotelbooking.hotelbookingapp.exception.UsernameAlreadyExistsException;
import com.hotelbooking.hotelbookingapp.model.dto.ResetPasswordDTO;
import com.hotelbooking.hotelbookingapp.model.dto.UserDTO;
import com.hotelbooking.hotelbookingapp.model.dto.UserRegistrationDTO;
import com.hotelbooking.hotelbookingapp.model.enums.RoleType;
import com.hotelbooking.hotelbookingapp.repository.CustomerRepository;
import com.hotelbooking.hotelbookingapp.repository.HotelManagerRepository;
import com.hotelbooking.hotelbookingapp.repository.RoleRepository;
import com.hotelbooking.hotelbookingapp.repository.UserRepository;
import com.hotelbooking.hotelbookingapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Added for potential use with streams if refactoring list mapping

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class UserServiceImpl implements UserService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final HotelManagerRepository hotelManagerRepository;
    private final PasswordEncoder passwordEncoder;

    // Manual constructor to replace @RequiredArgsConstructor
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           CustomerRepository customerRepository,
                           HotelManagerRepository hotelManagerRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.hotelManagerRepository = hotelManagerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User saveUser(UserRegistrationDTO registrationDTO) {
        log.info("Attempting to save a new user: {}", registrationDTO.getUsername());

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(registrationDTO.getUsername().trim()));
        if (existingUser.isPresent()) {
            log.warn("Username {} already exists.", registrationDTO.getUsername());
            throw new UsernameAlreadyExistsException("This username is already registered!");
        }

        User user = mapRegistrationDtoToUser(registrationDTO);
        // User is saved first to get an ID, then Customer/HotelManager
        User tempSavedUser = userRepository.save(user); // Save user to get ID for associations

        if (RoleType.CUSTOMER.equals(registrationDTO.getRoleType())) {
            // Replaced .builder() with constructor and setters for Customer
            Customer customer = new Customer();
            customer.setUser(tempSavedUser); // Use the user instance that has an ID
            customerRepository.save(customer);
            log.debug("Customer entity created for user {}", tempSavedUser.getUsername());
        } else if (RoleType.HOTEL_MANAGER.equals(registrationDTO.getRoleType())) {
            // Replaced .builder() with constructor and setters for HotelManager
            HotelManager hotelManager = new HotelManager();
            hotelManager.setUser(tempSavedUser); // Use the user instance that has an ID
            hotelManagerRepository.save(hotelManager);
            log.debug("HotelManager entity created for user {}", tempSavedUser.getUsername());
        }

        // The user entity might need to be re-saved if the Customer/HotelManager relationship
        // is bidirectional and owned by User, and not cascaded from Customer/HotelManager.
        // However, typically Customer/HotelManager would just hold a user_id.
        // For simplicity, we return the user instance that was saved.
        log.info("Successfully saved new user: {} with ID: {}", tempSavedUser.getUsername(), tempSavedUser.getId());
        return tempSavedUser; // Return the user instance that was actually persisted
    }

    @Override
    public User findUserByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDTO findUserDTOByUsername(String username) {
        log.debug("Finding user DTO by username: {}", username);
        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return mapUserToUserDto(user);
    }

    @Override
    public UserDTO findUserById(Long id) {
        log.debug("Finding user DTO by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        return mapUserToUserDto(user);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        log.debug("Finding all users");
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::mapUserToUserDto)
                .collect(Collectors.toList()); // More concise way to map list
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {
        log.info("Attempting to update user with ID: {}", userDTO.getId());

        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userDTO.getId()));

        if (usernameExistsAndNotSameUser(userDTO.getUsername(), user.getId())) {
            log.warn("Attempt to update user ID {} with a username '{}' that already exists for another user.", userDTO.getId(), userDTO.getUsername());
            throw new UsernameAlreadyExistsException("This username is already registered!");
        }

        setFormattedDataToUser(user, userDTO);
        userRepository.save(user);
        log.info("Successfully updated existing user with ID: {}", userDTO.getId());
    }

    @Override
    @Transactional
    public void updateLoggedInUser(UserDTO userDTO) {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByUsername(loggedInUsername);
        if (loggedInUser == null) {
            log.error("Logged in user {} not found in database for update.", loggedInUsername);
            throw new UsernameNotFoundException("Logged in user not found in database.");
        }
        log.info("Attempting to update logged in user with ID: {}", loggedInUser.getId());

        if (usernameExistsAndNotSameUser(userDTO.getUsername(), loggedInUser.getId())) {
            log.warn("Logged in user (ID: {}) attempted to change username to '{}' which already exists.", loggedInUser.getId(), userDTO.getUsername());
            throw new UsernameAlreadyExistsException("This username is already registered!");
        }

        setFormattedDataToUser(loggedInUser, userDTO);
        userRepository.save(loggedInUser);
        log.info("Successfully updated logged in user with ID: {}", loggedInUser.getId());

        // Update authentication context
        updateAuthentication(mapUserToUserDto(loggedInUser)); // Pass UserDTO to updateAuthentication
    }

    @Override
    @Transactional // Ensure transactional context for delete
    public void deleteUserById(Long id) {
        log.info("Attempting to delete user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing user with ID: {}", id);
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }

    @Override
    @Transactional
    public User resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = auth.getName();
        log.info("Attempting to reset password for user: {}", loggedInUsername);

        User user = userRepository.findByUsername(loggedInUsername);
        if (user == null) {
            log.error("User {} not found for password reset.", loggedInUsername);
            throw new UsernameNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), user.getPassword())) {
            log.warn("Incorrect old password attempt for user: {}", loggedInUsername);
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        User updatedUser = userRepository.save(user);
        log.info("Password successfully reset for user: {}", loggedInUsername);
        return updatedUser;
    }

    private User mapRegistrationDtoToUser(UserRegistrationDTO registrationDTO) {
        Role userRole = roleRepository.findByRoleType(registrationDTO.getRoleType());
        if (userRole == null) {
            log.error("RoleType {} not found in database.", registrationDTO.getRoleType());
            throw new EntityNotFoundException("Role not found for type: " + registrationDTO.getRoleType());
        }
        // Replaced .builder() with constructor and setters for User
        User user = new User();
        user.setUsername(registrationDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setName(formatText(registrationDTO.getName()));
        user.setLastName(formatText(registrationDTO.getLastName()));
        user.setRole(userRole);
        // createdDate is usually handled by @CreationTimestamp in User entity
        return user;
    }

    private UserDTO mapUserToUserDto(User user) {
        // Replaced .builder() with constructor and setters for UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRole(user.getRole());
        return userDTO;
    }

    private boolean usernameExistsAndNotSameUser(String username, Long userId) {
        if (username == null) return false;
        User existingUserWithSameUsername = userRepository.findByUsername(username.trim());
        return existingUserWithSameUsername != null && !existingUserWithSameUsername.getId().equals(userId);
    }

    private String formatText(String text) {
        if (text == null) {
            return null;
        }
        return StringUtils.capitalize(text.trim());
    }

    private void setFormattedDataToUser(User user, UserDTO userDTO) {
        user.setUsername(userDTO.getUsername().trim()); // Trim username
        user.setName(formatText(userDTO.getName()));
        user.setLastName(formatText(userDTO.getLastName()));
        // Role is not typically updated through this method, handle separately if needed.
        // If role needs to be updatable, ensure userDTO.getRole() is not null and fetch Role entity.
        // user.setRole(userDTO.getRole());
    }

    private void updateAuthentication(UserDTO userDTO) { // Changed parameter to UserDTO
        log.debug("Updating authentication context for user: {}", userDTO.getUsername());
        // Fetch the full User entity to ensure password and authorities are correct
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user == null) {
            log.error("Cannot update authentication context: User {} not found.", userDTO.getUsername());
            return; // Or throw an exception
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleType().name()));

        UserDetails newUserDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Use the persisted (encoded) password
                authorities
        );

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null, // Credentials typically set to null after authentication
                newUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        log.info("Authentication context updated for user: {}", user.getUsername());
    }
}