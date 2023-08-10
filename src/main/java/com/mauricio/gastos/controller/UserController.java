package com.mauricio.gastos.controller;

import java.util.List;

import com.mauricio.gastos.exceptions.UserExceptions;
import com.mauricio.gastos.exceptions.UserExceptions.UsernameExistException;
import com.mauricio.gastos.exceptions.UserExceptions.EmailExistException;
import com.mauricio.gastos.exceptions.UserExceptions.UserNotFoundException;
import com.mauricio.gastos.exceptions.UserExceptions.TokenNotValidException;
import com.mauricio.gastos.DTO.RoleDTO;
import com.mauricio.gastos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mauricio.gastos.DTO.UserDTO;
import static com.mauricio.gastos.util.Constants.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final EmailService emailService;

	private final RoleService roleService;

	private final UserService userService;

	@Autowired
	public UserController(EmailServiceImpl emailService,RoleServiceImpl roleService,UserService userService){
		this.emailService = emailService;
		this.roleService = roleService;
		this.userService = userService;
	}
	@GetMapping("/getUsers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDTO>> getUsers() {
		try {
			List<UserDTO> users = userService.getUsers();
			return ResponseEntity.ok(users);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/createUser")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
		try {
			UserDTO createdUser = userService.createUser(userDTO);
			emailService.mailSenderVerification(userDTO.getEmail(), userDTO.getUsername());
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (UsernameExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(USERNAME_EXIST_EXCEPTION);
		} catch (EmailExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(EMAIL_EXIST_EXCEPTION);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		try {
			return userService.deleteUser(userId);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}


	@PutMapping("/updateUser/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
		try {
			UserDTO updatedUser = userService.updateUser(userId, userDTO);
			return ResponseEntity.ok(updatedUser);
		}catch (UsernameExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(USERNAME_EXIST_EXCEPTION);
		} catch (EmailExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(EMAIL_EXIST_EXCEPTION);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(USER_NOT_FOUND_EXCEPTION,userId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getRoles")
	public ResponseEntity<List<RoleDTO>> getRoles() {
		try {
			return ResponseEntity.ok(roleService.getRoles());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(value = "/validUsername/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean validUsername(@PathVariable String username) {
		try {
			return userService.validUsername(username);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@PostMapping(value = "/validEmail", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean validEmail(@RequestBody String email){
		try {
			return userService.validEmail(email);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@PostMapping("/validTokenEmail/{token}")
	public ResponseEntity<?> validTokenEmail(@PathVariable String token){
		try {
			if(userService.validTokenEmail(token)){
				return ResponseEntity.ok().build();
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} catch (TokenNotValidException e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(TOKEN_NO_VALID_EXCEPTION);
        }
    }

	@GetMapping("/resendValidEmail/{username}")
	public ResponseEntity<?> resendValidationEmail(@PathVariable String username) {
		try {
			if (emailService.resendValidationEmail(username)) {
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		} catch (Exception ignored) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/emailIsVerify/{username}")
	public boolean emailIsValidated(@PathVariable String username){
			return userService.emailIsVerify(username);
	}
}
