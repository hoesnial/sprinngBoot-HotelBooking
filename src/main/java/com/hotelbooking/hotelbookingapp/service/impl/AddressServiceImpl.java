package com.hotelbooking.hotelbookingapp.service.impl;

import com.hotelbooking.hotelbookingapp.model.Address;
import com.hotelbooking.hotelbookingapp.model.dto.AddressDTO;
import com.hotelbooking.hotelbookingapp.repository.AddressRepository;
import com.hotelbooking.hotelbookingapp.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor; // Lombok annotation removed
// import lombok.extern.slf4j.Slf4j; // Lombok annotation removed
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
// @RequiredArgsConstructor // Lombok annotation removed
// @Slf4j // Lombok annotation removed
public class AddressServiceImpl implements AddressService {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;

    // Manual constructor to replace @RequiredArgsConstructor
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address saveAddress(AddressDTO addressDTO) {
        log.info("Attempting to save a new address: {}", addressDTO.toString());
        Address address = mapAddressDtoToAddress(addressDTO);

        Address savedAddress = addressRepository.save(address);
        log.info("Successfully saved new address with ID: {}", savedAddress.getId()); // Corrected to use savedAddress.getId()
        return savedAddress;
    }

    @Override
    public AddressDTO findAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with ID: " + id)); // Added ID to message

        return mapAddressToAddressDto(address);
    }

    @Override
    public Address updateAddress(AddressDTO addressDTO) {
        log.info("Attempting to update address with ID: {}", addressDTO.getId());
        Address existingAddress = addressRepository.findById(addressDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found with ID: " + addressDTO.getId())); // Added ID to message

        setFormattedDataToAddress(existingAddress, addressDTO);

        Address updatedAddress = addressRepository.save(existingAddress);
        log.info("Successfully updated address with ID: {}", existingAddress.getId());
        return updatedAddress;
    }

    @Override
    public void deleteAddress(Long id) {
        log.info("Attempting to delete address with ID: {}", id);
        if (!addressRepository.existsById(id)) {
            log.error("Failed to delete address. Address with ID: {} not found", id);
            throw new EntityNotFoundException("Address not found with ID: " + id); // Added ID to message
        }
        addressRepository.deleteById(id);
        log.info("Successfully deleted address with ID: {}", id);
    }

    @Override
    public Address mapAddressDtoToAddress(AddressDTO dto) {
        // Replaced .builder() with constructor and setters
        Address address = new Address();
        address.setAddressLine(formatText(dto.getAddressLine()));
        address.setCity(formatText(dto.getCity()));
        address.setCountry(formatText(dto.getCountry()));
        // Assuming 'id' is auto-generated for new Address entities and not set from DTO here
        return address;
    }

    @Override
    public AddressDTO mapAddressToAddressDto(Address address) {
        // Replaced .builder() with constructor and setters
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setAddressLine(address.getAddressLine());
        addressDTO.setCity(address.getCity());
        addressDTO.setCountry(address.getCountry());
        return addressDTO;
    }

    private String formatText(String text) {
        if (text == null) {
            return null;
        }
        return StringUtils.capitalize(text.trim());
    }

    private void setFormattedDataToAddress(Address address, AddressDTO addressDTO) {
        address.setAddressLine(formatText(addressDTO.getAddressLine()));
        address.setCity(formatText(addressDTO.getCity()));
        address.setCountry(formatText(addressDTO.getCountry()));
    }
}