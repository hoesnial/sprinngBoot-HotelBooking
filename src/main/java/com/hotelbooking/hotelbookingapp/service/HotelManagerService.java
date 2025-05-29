package com.hotelbooking.hotelbookingapp.service;

import com.hotelbooking.hotelbookingapp.model.HotelManager;
import com.hotelbooking.hotelbookingapp.model.User;

public interface HotelManagerService {

    HotelManager findByUser(User user);

}
