package com.quicknote.api.controller;

import com.quicknote.api.dto.NoteRequestDTO;
import com.quicknote.api.dto.PinNoteRequestDTO;
import com.quicknote.api.model.Note;
import com.quicknote.api.service.NoteService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

	private final NoteService noteService;

	@GetMapping
	public ResponseEntity<List<Note>> getNotes(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		List<Note> notes = noteService.getAllNotes(authorizationHeader);
		return ResponseEntity.ok(notes);
	}

	@PostMapping
	public ResponseEntity<Note> createNote(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@Valid @RequestBody NoteRequestDTO requestDTO) {
		Note created = noteService.createNote(authorizationHeader, requestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Note> updateNoteById(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable Long id,
			@Valid @RequestBody NoteRequestDTO requestDTO) {
		Note updated = noteService.updateNoteById(authorizationHeader, id, requestDTO);
		return ResponseEntity.ok(updated);
	}

	@PutMapping("/{id}/pin")
	public ResponseEntity<Note> pinNoteById(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable Long id,
			@Valid @RequestBody PinNoteRequestDTO requestDTO) {
		Note updated = noteService.pinNoteById(authorizationHeader, id, requestDTO);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteNoteById(
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@PathVariable Long id) {
		noteService.deleteNoteById(authorizationHeader, id);
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("message", "Note deleted successfully");
		body.put("id", id);
		return ResponseEntity.ok(body);
	}
}