package com.admindashboard.usermanagement;

import com.admindashboard.usermanagement.*;
import com.admindashboard.exception.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
    	// In UserService.java
    	if (userRepository.existsByEmail(userDTO.getEmail())) {
    	    throw new DuplicateEntityException("User", "email", userDTO.getEmail());
    	}
    	if (userDTO.getPhoneNumber() != null && 
    	    userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
    	    throw new DuplicateEntityException("User", "phone number", userDTO.getPhoneNumber());
    	}

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
        user.setUserType(userDTO.getUserType());

        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId, "USER_003"));
    }

    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) {
        User user = getUserById(userId);

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new DuplicateEntityException("Email already exists", "USER_001");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
                throw new DuplicateEntityException("Phone number already exists", "USER_002");
            }
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(user.getLastName());
        if (userDTO.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getProfilePictureUrl() != null) user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
        if (userDTO.getUserType() != null) user.setUserType(userDTO.getUserType());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId, "USER_003");
        }
        userRepository.deleteById(userId);
    }
}