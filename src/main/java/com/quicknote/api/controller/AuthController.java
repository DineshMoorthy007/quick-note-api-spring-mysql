package com.quicknote.api.controller;

import com.quicknote.api.dto.AuthRequestDTO;
import com.quicknote.api.dto.AuthResponseDTO;
import com.quicknote.api.exception.ResourceNotFoundException;
import com.quicknote.api.model.User;
import com.quicknote.api.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User is not registered. Please register first."));

		return ResponseEntity.ok(toResponse(user));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseGet(() -> userRepository.save(User.builder()
						.username(request.getUsername())
						.password(request.getPassword())
						.build()));
		return ResponseEntity.ok(toResponse(user));
	}

	private AuthResponseDTO toResponse(User user) {
		UUID userId = user.getId();
		String userIdValue = userId.toString();
		return AuthResponseDTO.builder()
				.token(userIdValue)
				.userId(userIdValue)
				.username(user.getUsername())
				.build();
	}
}