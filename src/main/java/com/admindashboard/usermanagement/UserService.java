package com.admindashboard.usermanagement;

import com.admindashboard.enums.UserType;
import com.admindashboard.exception.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateEntityException("User", "email", userDTO.getEmail());
        }
        if (userDTO.getPhoneNumber() != null && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DuplicateEntityException("User", "phone number", userDTO.getPhoneNumber());
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setUserType(UserType.valueOf(userDTO.getUserType()));
        // Note: profilePictureUrl is not in the DTO, so we will omit it for now
        // user.setProfilePictureUrl(userDTO.getProfilePictureUrl());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) {
        User user = getUserById(userId);

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new DuplicateEntityException("User", "email", userDTO.getEmail());
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
                throw new DuplicateEntityException("User", "phone number", userDTO.getPhoneNumber());
            }
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());
        if (userDTO.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        if (userDTO.getUserType() != null) user.setUserType(UserType.valueOf(userDTO.getUserType()));
        // Note: profilePictureUrl is not in the DTO, so we will omit it for now

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }
}