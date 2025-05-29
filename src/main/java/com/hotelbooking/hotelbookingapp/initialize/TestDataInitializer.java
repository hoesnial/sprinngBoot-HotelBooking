package com.hotelbooking.hotelbookingapp.initialize;

import java.time.LocalDate;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hotelbooking.hotelbookingapp.model.Address;
import com.hotelbooking.hotelbookingapp.model.Admin;
import com.hotelbooking.hotelbookingapp.model.Availability;
import com.hotelbooking.hotelbookingapp.model.Customer;
import com.hotelbooking.hotelbookingapp.model.Hotel; // Pastikan import Arrays ada
import com.hotelbooking.hotelbookingapp.model.HotelManager;
import com.hotelbooking.hotelbookingapp.model.Role;
import com.hotelbooking.hotelbookingapp.model.Room;
import com.hotelbooking.hotelbookingapp.model.User;
import com.hotelbooking.hotelbookingapp.model.enums.RoleType;
import com.hotelbooking.hotelbookingapp.model.enums.RoomType;
import com.hotelbooking.hotelbookingapp.repository.AddressRepository;
import com.hotelbooking.hotelbookingapp.repository.AdminRepository;
import com.hotelbooking.hotelbookingapp.repository.AvailabilityRepository;
import com.hotelbooking.hotelbookingapp.repository.CustomerRepository;
import com.hotelbooking.hotelbookingapp.repository.HotelManagerRepository;
import com.hotelbooking.hotelbookingapp.repository.HotelRepository;
import com.hotelbooking.hotelbookingapp.repository.RoleRepository;
import com.hotelbooking.hotelbookingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
// import java.util.HashSet; // Tidak lagi diperlukan jika hanya menggunakan ArrayList/List
// import java.util.ArrayList; // Tidak perlu import eksplisit jika tidak membuat instance baru ArrayList di sini untuk rooms

@Component
public class TestDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TestDataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final HotelManagerRepository hotelManagerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final HotelRepository hotelRepository;
    private final AvailabilityRepository availabilityRepository;

    public TestDataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                               AdminRepository adminRepository, CustomerRepository customerRepository,
                               HotelManagerRepository hotelManagerRepository, PasswordEncoder passwordEncoder,
                               AddressRepository addressRepository, HotelRepository hotelRepository,
                               AvailabilityRepository availabilityRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
        this.hotelManagerRepository = hotelManagerRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.hotelRepository = hotelRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        try {
            log.warn("Checking if test data persistence is required...");

            if (roleRepository.count() == 0 && userRepository.count() == 0) {
                log.info("Initiating test data persistence");

                Role adminRole = new Role(RoleType.ADMIN);
                Role customerRole = new Role(RoleType.CUSTOMER);
                Role hotelManagerRole = new Role(RoleType.HOTEL_MANAGER);

                roleRepository.save(adminRole);
                roleRepository.save(customerRole);
                roleRepository.save(hotelManagerRole);
                log.info("Role data persisted");

                User user1 = new User();
                user1.setUsername("admin@hotel.com");
                user1.setPassword(passwordEncoder.encode("1"));
                user1.setName("Admin");
                user1.setLastName("Admin");
                user1.setRole(adminRole);

                User user2 = new User();
                user2.setUsername("customer1@hotel.com");
                user2.setPassword(passwordEncoder.encode("1"));
                user2.setName("Kaya Alp");
                user2.setLastName("Koker");
                user2.setRole(customerRole);

                User user3 = new User();
                user3.setUsername("manager1@hotel.com");
                user3.setPassword(passwordEncoder.encode("1"));
                user3.setName("John");
                user3.setLastName("Doe");
                user3.setRole(hotelManagerRole);

                User user4 = new User();
                user4.setUsername("manager2@hotel.com");
                user4.setPassword(passwordEncoder.encode("1"));
                user4.setName("Max");
                user4.setLastName("Mustermann");
                user4.setRole(hotelManagerRole);

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);

                Admin admin1 = new Admin();
                admin1.setUser(user1);

                Customer c1 = new Customer();
                c1.setUser(user2);

                HotelManager hm1 = new HotelManager();
                hm1.setUser(user3);

                HotelManager hm2 = new HotelManager();
                hm2.setUser(user4);

                adminRepository.save(admin1);
                customerRepository.save(c1);
                hotelManagerRepository.save(hm1);
                hotelManagerRepository.save(hm2);
                log.info("User data persisted");

                Address addressIst1 = new Address();
                addressIst1.setAddressLine("Acısu Sokağı No:19, 34357");
                addressIst1.setCity("Istanbul");
                addressIst1.setCountry("Turkey");

                Address addressIst2 = new Address();
                addressIst2.setAddressLine("Çırağan Cd. No:28, 34349 Beşiktaş");
                addressIst2.setCity("Istanbul");
                addressIst2.setCountry("Turkey");

                Address addressIst3 = new Address();
                addressIst3.setAddressLine("Çırağan Cd. No:32, 34349 Beşiktaş");
                addressIst3.setCity("Istanbul");
                addressIst3.setCountry("Turkey");

                Address addressBerlin1 = new Address();
                addressBerlin1.setAddressLine("Unter den Linden 77");
                addressBerlin1.setCity("Berlin");
                addressBerlin1.setCountry("Germany");

                Address addressBerlin2 = new Address();
                addressBerlin2.setAddressLine("Potsdamer Platz 3, Mitte, 10785");
                addressBerlin2.setCity("Berlin");
                addressBerlin2.setCountry("Germany");

                Address addressBerlin3 = new Address();
                addressBerlin3.setAddressLine("Budapester Str. 2, Mitte, 10787");
                addressBerlin3.setCity("Berlin");
                addressBerlin3.setCountry("Germany");

                addressRepository.save(addressIst1);
                addressRepository.save(addressIst2);
                addressRepository.save(addressIst3);
                addressRepository.save(addressBerlin1);
                addressRepository.save(addressBerlin2);
                addressRepository.save(addressBerlin3);

                Hotel hotelIst1 = new Hotel();
                hotelIst1.setName("Swissotel The Bosphorus Istanbul");
                hotelIst1.setAddress(addressIst1);
                hotelIst1.setHotelManager(hm1);
                // Tidak perlu `if (hotelIst1.getRooms() == null)` karena Hotel.java sudah menginisialisasi

                Hotel hotelIst2 = new Hotel();
                hotelIst2.setName("Four Seasons Hotel Istanbul");
                hotelIst2.setAddress(addressIst2);
                hotelIst2.setHotelManager(hm1);

                Hotel hotelIst3 = new Hotel();
                hotelIst3.setName("Ciragan Palace Kempinski Istanbul");
                hotelIst3.setAddress(addressIst3);
                hotelIst3.setHotelManager(hm1);

                Hotel hotelBerlin1 = new Hotel();
                hotelBerlin1.setName("Hotel Adlon Kempinski Berlin");
                hotelBerlin1.setAddress(addressBerlin1);
                hotelBerlin1.setHotelManager(hm2);

                Hotel hotelBerlin2 = new Hotel();
                hotelBerlin2.setName("The Ritz-Carlton Berlin");
                hotelBerlin2.setAddress(addressBerlin2);
                hotelBerlin2.setHotelManager(hm2);

                Hotel hotelBerlin3 = new Hotel();
                hotelBerlin3.setName("InterContinental Berlin");
                hotelBerlin3.setAddress(addressBerlin3);
                hotelBerlin3.setHotelManager(hm2);

                Room singleRoomIst1 = new Room();
                singleRoomIst1.setRoomType(RoomType.SINGLE);
                singleRoomIst1.setPricePerNight(370.0);
                singleRoomIst1.setRoomCount(35);
                singleRoomIst1.setHotel(hotelIst1);

                Room doubleRoomIst1 = new Room();
                doubleRoomIst1.setRoomType(RoomType.DOUBLE);
                doubleRoomIst1.setPricePerNight(459.0);
                doubleRoomIst1.setRoomCount(45);
                doubleRoomIst1.setHotel(hotelIst1);

                Room singleRoomIst2 = new Room();
                singleRoomIst2.setRoomType(RoomType.SINGLE);
                singleRoomIst2.setPricePerNight(700.0);
                singleRoomIst2.setRoomCount(25);
                singleRoomIst2.setHotel(hotelIst2);

                Room doubleRoomIst2 = new Room();
                doubleRoomIst2.setRoomType(RoomType.DOUBLE);
                doubleRoomIst2.setPricePerNight(890.0);
                doubleRoomIst2.setRoomCount(30);
                doubleRoomIst2.setHotel(hotelIst2);

                Room singleRoomIst3 = new Room();
                singleRoomIst3.setRoomType(RoomType.SINGLE);
                singleRoomIst3.setPricePerNight(691.0);
                singleRoomIst3.setRoomCount(30);
                singleRoomIst3.setHotel(hotelIst3);

                Room doubleRoomIst3 = new Room();
                doubleRoomIst3.setRoomType(RoomType.DOUBLE);
                doubleRoomIst3.setPricePerNight(800.0);
                doubleRoomIst3.setRoomCount(75);
                doubleRoomIst3.setHotel(hotelIst3);

                Room singleRoomBerlin1 = new Room();
                singleRoomBerlin1.setRoomType(RoomType.SINGLE);
                singleRoomBerlin1.setPricePerNight(120.0);
                singleRoomBerlin1.setRoomCount(25);
                singleRoomBerlin1.setHotel(hotelBerlin1);

                Room doubleRoomBerlin1 = new Room();
                doubleRoomBerlin1.setRoomType(RoomType.DOUBLE);
                doubleRoomBerlin1.setPricePerNight(250.0);
                doubleRoomBerlin1.setRoomCount(15);
                doubleRoomBerlin1.setHotel(hotelBerlin1);

                Room singleRoomBerlin2 = new Room();
                singleRoomBerlin2.setRoomType(RoomType.SINGLE);
                singleRoomBerlin2.setPricePerNight(300.0);
                singleRoomBerlin2.setRoomCount(50);
                singleRoomBerlin2.setHotel(hotelBerlin2);

                Room doubleRoomBerlin2 = new Room();
                doubleRoomBerlin2.setRoomType(RoomType.DOUBLE);
                doubleRoomBerlin2.setPricePerNight(400.0);
                doubleRoomBerlin2.setRoomCount(50);
                doubleRoomBerlin2.setHotel(hotelBerlin2);

                Room singleRoomBerlin3 = new Room();
                singleRoomBerlin3.setRoomType(RoomType.SINGLE);
                singleRoomBerlin3.setPricePerNight(179.0);
                singleRoomBerlin3.setRoomCount(45);
                singleRoomBerlin3.setHotel(hotelBerlin3);

                Room doubleRoomBerlin3 = new Room();
                doubleRoomBerlin3.setRoomType(RoomType.DOUBLE);
                doubleRoomBerlin3.setPricePerNight(256.0);
                doubleRoomBerlin3.setRoomCount(25);
                doubleRoomBerlin3.setHotel(hotelBerlin3);

                // Menggunakan helper method addRoom dari Hotel.java jika ada,
                // atau langsung menggunakan getRooms().addAll() jika Hotel.java menggunakan List
                // hotelIst1.addRoom(singleRoomIst1); // Jika menggunakan helper method
                // hotelIst1.addRoom(doubleRoomIst1);
                hotelIst1.getRooms().addAll(Arrays.asList(singleRoomIst1, doubleRoomIst1));
                hotelIst2.getRooms().addAll(Arrays.asList(singleRoomIst2, doubleRoomIst2));
                hotelIst3.getRooms().addAll(Arrays.asList(singleRoomIst3, doubleRoomIst3));
                hotelBerlin1.getRooms().addAll(Arrays.asList(singleRoomBerlin1, doubleRoomBerlin1));
                hotelBerlin2.getRooms().addAll(Arrays.asList(singleRoomBerlin2, doubleRoomBerlin2));
                hotelBerlin3.getRooms().addAll(Arrays.asList(singleRoomBerlin3, doubleRoomBerlin3));

                hotelRepository.save(hotelIst1);
                hotelRepository.save(hotelIst2);
                hotelRepository.save(hotelIst3);
                hotelRepository.save(hotelBerlin1);
                hotelRepository.save(hotelBerlin2);
                hotelRepository.save(hotelBerlin3);
                log.info("Hotel data persisted");

                Availability av1Berlin1 = new Availability();
                av1Berlin1.setHotel(hotelBerlin1);
                av1Berlin1.setDate(LocalDate.of(2023, 9, 1));
                av1Berlin1.setRoom(singleRoomBerlin1);
                av1Berlin1.setAvailableRooms(5);

                Availability av2Berlin1 = new Availability();
                av2Berlin1.setHotel(hotelBerlin1);
                av2Berlin1.setDate(LocalDate.of(2023, 9, 2));
                av2Berlin1.setRoom(doubleRoomBerlin1);
                av2Berlin1.setAvailableRooms(7);

                availabilityRepository.save(av1Berlin1);
                availabilityRepository.save(av2Berlin1);
                log.info("Availability data persisted");

            } else {
                log.info("Test data persistence is not required");
            }
            log.warn("App ready");
        } catch (DataAccessException e) {
            log.error("Exception occurred during data persistence: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected exception occurred: " + e.getMessage(), e);
        }
    }
}