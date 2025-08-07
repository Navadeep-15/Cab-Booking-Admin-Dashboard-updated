package com.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admindashboard.exception.*;
import com.admindashboard.usermanagement.*;
import com.admindashboard.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserType(UserType.PASSENGER);

        userDTO = new UserDTO();
        userDTO.setEmail("new_user@test.com");
        userDTO.setPassword("password123");
        userDTO.setFirstName("New");
        userDTO.setLastName("User");
        userDTO.setPhoneNumber("9876543210");
        userDTO.setUserType("PASSENGER");
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
    
    @Test
    void testCreateUserWithDuplicateEmail() throws Exception {
        doThrow(new DuplicateEntityException("User", "email", userDTO.getEmail()))
                .when(userService).createUser(any(UserDTO.class));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        List<User> userList = List.of(user);
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Test"));
    }

    @Test
    void testGetUserByIdSuccess() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new EntityNotFoundException("User", 99L));

        mockMvc.perform(get("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        long userId = 1L;
        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("Updated");
        updateDTO.setEmail("updated@test.com");
        updateDTO.setLastName("User"); // Corrected
        updateDTO.setUserType("ADMIN");
        updateDTO.setPassword("newPassword123");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setEmail("updated@test.com");
        updatedUser.setUserType(UserType.ADMIN);

        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User", 99L)).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
