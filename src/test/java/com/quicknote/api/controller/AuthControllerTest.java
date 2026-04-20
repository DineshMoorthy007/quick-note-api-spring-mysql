package com.quicknote.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.quicknote.api.exception.GlobalExceptionHandler;
import com.quicknote.api.model.User;
import com.quicknote.api.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthController authController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(authController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void loginShouldReturnExistingUserResponse() throws Exception {
		UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		User existingUser = User.builder()
				.id(userId)
				.username("alice")
				.password("secret")
				.build();
		when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existingUser));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"alice\",\"password\":\"secret\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(userId.toString()))
				.andExpect(jsonPath("$.userId").value(userId.toString()))
				.andExpect(jsonPath("$.username").value("alice"));
	}

	@Test
	void loginShouldReturn404WhenUserIsMissing() throws Exception {
		when(userRepository.findByUsername(eq("bob"))).thenReturn(Optional.empty());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"bob\",\"password\":\"secret\"}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("User is not registered. Please register first."));
	}

	@Test
	void registerShouldCreateUserWhenMissing() throws Exception {
		UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
		User createdUser = User.builder()
				.id(userId)
				.username("bob")
				.password("secret")
				.build();
		when(userRepository.findByUsername(eq("bob"))).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(createdUser);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"bob\",\"password\":\"secret\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(userId.toString()))
				.andExpect(jsonPath("$.userId").value(userId.toString()))
				.andExpect(jsonPath("$.username").value("bob"));

		verify(userRepository).save(any(User.class));
	}

	@Test
	void loginShouldReturn400ForInvalidPayload() throws Exception {
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"\",\"password\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}
}
