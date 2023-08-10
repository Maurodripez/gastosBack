package com.mauricio.gastos.service;

import com.mauricio.gastos.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import com.mauricio.gastos.exceptions.UserExceptions.UsernameExistException;
import com.mauricio.gastos.exceptions.UserExceptions.TokenNotValidException;
import com.mauricio.gastos.exceptions.UserExceptions.EmailExistException;
import com.mauricio.gastos.exceptions.UserExceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> getUsers();

    UserDTO createUser(UserDTO userDTO) throws UsernameExistException, EmailExistException;

    ResponseEntity<?> deleteUser(Long userId) throws UserNotFoundException;

    UserDTO updateUser(Long userId, UserDTO userDTO) throws UserNotFoundException, EmailExistException, UsernameExistException;

    boolean validUsername(String username);

    boolean validEmail(String email);

    boolean validTokenEmail(String token) throws TokenNotValidException;

    boolean emailIsVerify(String username);
}
