package com.quicknote.api.controller;

import com.quicknote.api.dto.AuthRequestDTO;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthRequestDTO request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Registered successfully");
		body.put("token", request.getEmail());
		return ResponseEntity.ok(body);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequestDTO request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Login successful");
		body.put("token", request.getEmail());
		return ResponseEntity.ok(body);
	}
}