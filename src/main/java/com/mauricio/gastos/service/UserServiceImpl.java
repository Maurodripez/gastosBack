package com.mauricio.gastos.service;
import com.mauricio.gastos.exceptions.UserExceptions.UsernameExistException;
import com.mauricio.gastos.exceptions.UserExceptions.EmailExistException;
import com.mauricio.gastos.exceptions.UserExceptions.UserNotFoundException;
import com.mauricio.gastos.exceptions.UserExceptions.TokenNotValidException;
import com.mauricio.gastos.DTO.UserDTO;
import com.mauricio.gastos.models.ERole;
import com.mauricio.gastos.models.RoleEntity;
import com.mauricio.gastos.models.UserEntity;
import com.mauricio.gastos.repositories.UserRepository;
import com.mauricio.gastos.securityjwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.mauricio.gastos.util.Constants.*;

@Service
public class UserServiceImpl implements UserService {

    public UserServiceImpl(){}
    JwtUtils jwtUtils;

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(JwtUtils jwtUtils,PasswordEncoder passwordEncoder,UserRepository userRepository){
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }




    @Override
    public List<UserDTO> getUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(this::showUser)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) throws UsernameExistException, EmailExistException {

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameExistException(USERNAME_EXIST_EXCEPTION);
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailExistException(EMAIL_EXIST_EXCEPTION);
        }

        Set<RoleEntity> roles = userDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .lastname(userDTO.getLastname())
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return showUser(userEntity);
    }

    @Override
    public ResponseEntity<?> deleteUser(Long userId) throws UserNotFoundException {

            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION, userId));
            }

            // Eliminar el usuario
            userRepository.deleteById(userId);
            return ResponseEntity.ok().build();


    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) throws UserNotFoundException, EmailExistException, UsernameExistException {

            Optional<UserEntity> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();

                // Verificar si ya existe un usuario con el mismo nombre de usuario
                if (userDTO.getUsername() != null &&
                        userRepository.existsByUsernameAndIdNot(userDTO.getUsername(), userId)) {
                    throw new UsernameExistException (USERNAME_EXIST_EXCEPTION);
                }

                // Verificar si ya existe un usuario con el mismo correo electrónico
                if (userDTO.getEmail() != null &&
                        userRepository.existsByEmailAndIdNot(userDTO.getEmail(), userId)) {
                    throw new EmailExistException(EMAIL_EXIST_EXCEPTION);
                }

                // Actualizar los campos del usuario con los valores de userDTO, si están presentes
                if (userDTO.getUsername() != null) {
                    user.setUsername(userDTO.getUsername());
                }
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail());
                }
                if (userDTO.getPassword() != null) {
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                }
                if (userDTO.getRoles() != null) {
                    // Obtener los roles existentes del usuario
                    Set<RoleEntity> existingRoles = user.getRoles();
                    // Obtener los nombres de los roles nuevos
                    Set<String> newRoleNames = userDTO.getRoles();
                    // Crear un conjunto de nuevos roles basado en los nombres de los roles nuevos
                    Set<RoleEntity> newRoles = newRoleNames.stream()
                            .map(roleName -> RoleEntity.builder()
                                    .name(ERole.valueOf(roleName))
                                    .build())
                            .collect(Collectors.toSet());
                    // Actualizar los roles del usuario con los nuevos roles
                    existingRoles.clear();
                    existingRoles.addAll(newRoles);
                }

                UserEntity updatedUser = userRepository.save(user);
                UserDTO userDTOWithToken = showUser(updatedUser);
                userDTOWithToken.setToken(jwtUtils.generateAccessToken(userDTOWithToken.getUsername()));
                return userDTOWithToken;
            } else {
                throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION, userId));
            }
    }

    @Override
    public boolean validUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean validEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean validTokenEmail(String token) throws TokenNotValidException {
        if(jwtUtils.isTokenValid(token)){
            String username = jwtUtils.getUsernameFromToken(token);
            if(userRepository.existsByUsername(username) && !userRepository.isVerify(username)){
               userRepository.changeVerify(username, true);
               return true;
            }
        }
        throw new TokenNotValidException(TOKEN_NO_VALID_EXCEPTION);
    }

    @Override
    public boolean emailIsVerify(String username) {
        return userRepository.emailIsVerify(username) > 0;
    }

    //De entity a DTO
    private UserDTO showUser(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setName(userEntity.getName());
        userDTO.setLastname(userEntity.getLastname());
        Set<String> roles = userEntity.getRoles().stream()
                .map(roleEntity -> roleEntity.getName().toString())
                .collect(Collectors.toSet());

        userDTO.setRoles(roles);

        return userDTO;
    }
}
