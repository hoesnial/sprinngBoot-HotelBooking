package com.hotelbooking.hotelbookingapp.service;

import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.model.dto.ResetPasswordDTO;
import com.hotelbooking.hotelbookingapp.model.dto.UserDTO;
import com.hotelbooking.hotelbookingapp.model.dto.UserRegistrationDTO;

import java.util.List;

public interface UserService {

    User saveUser(UserRegistrationDTO registrationDTO);

    // For registration
    User findUserByUsername(String username);

    UserDTO findUserDTOByUsername(String username);

    UserDTO findUserById(Long id);

    List<UserDTO> findAllUsers();

    void updateUser(UserDTO userDTO);

    void updateLoggedInUser(UserDTO userDTO);

    void deleteUserById(Long id);

    User resetPassword(ResetPasswordDTO resetPasswordDTO);

}
