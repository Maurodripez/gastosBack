package com.mauricio.gastos.controller;

import java.util.List;

import com.mauricio.gastos.DTO.RoleDTO;
import com.mauricio.gastos.service.EmailServiceImpl;
import com.mauricio.gastos.service.RoleServiceImpl;
import com.mauricio.gastos.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mauricio.gastos.DTO.UserDTO;
import com.mauricio.gastos.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	EmailServiceImpl emailService;
	@Autowired
	private RoleServiceImpl roleService;

	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	UserRepository userRepository;

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
	public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
		try {
			UserDTO createdUser = userService.createUser(userDTO);
			emailService.mailSenderVerification(userDTO.getEmail(), userDTO.getUsername());
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		try {
			return userService.deleteUser(userId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	@PutMapping("/updateUser/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
		try {
			UserDTO updatedUser = userService.updateUser(userId, userDTO);
			return ResponseEntity.ok(updatedUser);
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
		if(userService.validTokenEmail(token)){
			return ResponseEntity.ok().build();
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}


}
