package com.quicknote.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			BindException.class,
			MethodArgumentTypeMismatchException.class,
			IllegalArgumentException.class
	})
	public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest request) {
		String message = resolveBadRequestMessage(ex);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(
			HttpStatus status,
			String message,
			String path) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", path);

		return ResponseEntity.status(status).body(body);
	}

	private String resolveBadRequestMessage(Exception ex) {
		if (ex instanceof MethodArgumentNotValidException methodArgumentEx
				&& methodArgumentEx.getBindingResult().getFieldError() != null) {
			return methodArgumentEx.getBindingResult().getFieldError().getDefaultMessage();
		}

		if (ex instanceof BindException bindEx && bindEx.getBindingResult().getFieldError() != null) {
			return bindEx.getBindingResult().getFieldError().getDefaultMessage();
		}

		return ex.getMessage();
	}
}