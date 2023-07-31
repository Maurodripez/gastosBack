package com.mauricio.gastos.service;

import com.mauricio.gastos.DTO.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> getUsers();

    UserDTO createUser(UserDTO userDTO);

    ResponseEntity<?> deleteUser(Long userId);

    UserDTO updateUser(Long userId, UserDTO userDTO);

    boolean validUsername(String username);

    boolean validEmail(String email);

    boolean validTokenEmail(String token);

    boolean emailIsVerify(String username);
}
