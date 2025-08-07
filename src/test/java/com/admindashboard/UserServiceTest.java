package com.admindashboard;


import com.admindashboard.exception.*;
import com.admindashboard.usermanagement.*;
import com.admindashboard.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Setup a mock User entity
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserType(UserType.PASSENGER);
        user.setPasswordHash("hashed_password");

        // Setup a mock UserDTO
        userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("password123");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setUserType("PASSENGER");
    }

    @Test
    void testCreateUserSuccess() {
        // Mock the behavior of the repository and password encoder
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        User createdUser = userService.createUser(userDTO);

        // Assertions
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        // Mock the repository to simulate an existing email
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Assert that a DuplicateEntityException is thrown
        assertThrows(DuplicateEntityException.class, () -> userService.createUser(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllUsersSuccess() {
        // Mock the repository to return a list of users
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Call the service method
        List<User> users = userService.getAllUsers();

        // Assertions
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdSuccess() {
        // Mock the repository to return a user
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the service method
        User foundUser = userService.getUserById(1L);

        // Assertions
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Mock the repository to return an empty Optional
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that an EntityNotFoundException is thrown
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testUpdateUserSuccess() {
        // Create a DTO with fields to update
        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setLastName("Name");
        updateDTO.setPhoneNumber("0987654321");
        updateDTO.setEmail("updated@test.com");
        updateDTO.setUserType("ADMIN");

        // Mock the repository and password encoder
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        User updatedUser = userService.updateUser(1L, updateDTO);

        // Assertions
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("0987654321", updatedUser.getPhoneNumber());
        assertEquals("updated@test.com", updatedUser.getEmail());
        assertEquals(UserType.ADMIN, updatedUser.getUserType());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testUpdateUserNotFound() {
        // Create a DTO with fields to update
        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("Updated");

        // Mock the repository to return an empty Optional
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that an EntityNotFoundException is thrown
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, updateDTO));
    }

    @Test
    void testDeleteUserSuccess() {
        // Mock the repository to simulate a user existing
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Call the service method and assert no exception is thrown
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        // Mock the repository to simulate a user not existing
        when(userRepository.existsById(1L)).thenReturn(false);

        // Assert that an EntityNotFoundException is thrown
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
