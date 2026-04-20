package com.quicknote.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequestDTO {

	@Email(message = "email must be valid")
	@NotBlank(message = "email is required")
	private String email;

	@NotBlank(message = "password is required")
	private String password;
}