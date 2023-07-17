package com.mauricio.gastos.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.mauricio.gastos.DTO.RoleDTO;
import com.mauricio.gastos.models.ERole;
import com.mauricio.gastos.service.RoleServiceImpl;
import com.mauricio.gastos.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
		try {
			UserDTO createdUser = userService.createUser(userDTO);
			return ResponseEntity.ok(createdUser);
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


}
