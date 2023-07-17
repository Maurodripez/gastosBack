package com.mauricio.gastos.DTO;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	private Long id;

	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@Email
	@NotBlank
	private String email;
	
	private Set<String> roles;

	private String token;
}
