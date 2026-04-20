package com.quicknote.api.service;

import com.quicknote.api.dto.NoteRequestDTO;
import com.quicknote.api.dto.PinNoteRequestDTO;
import com.quicknote.api.exception.ResourceNotFoundException;
import com.quicknote.api.model.Note;
import com.quicknote.api.repository.NoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteService {

	private final NoteRepository noteRepository;

	@Transactional
	public Note createNote(String authorizationHeader, NoteRequestDTO requestDTO) {
		String userId = resolveUserIdFromAuthorizationHeader(authorizationHeader);
		Note note = toEntity(requestDTO, userId);
		return noteRepository.save(note);
	}

	@Transactional(readOnly = true)
	public List<Note> getAllNotes(String authorizationHeader) {
		String userId = resolveUserIdFromAuthorizationHeader(authorizationHeader);
		return noteRepository.findByUserId(userId);
	}

	@Transactional
	public Note updateNoteById(String authorizationHeader, Long id, NoteRequestDTO requestDTO) {
		String userId = resolveUserIdFromAuthorizationHeader(authorizationHeader);
		Note note = getExistingNoteForUser(id, userId);
		note.setTitle(requestDTO.getTitle());
		note.setContent(requestDTO.getContent());
		return noteRepository.save(note);
	}

	@Transactional
	public Note pinNoteById(String authorizationHeader, Long id, PinNoteRequestDTO pinNoteRequestDTO) {
		String userId = resolveUserIdFromAuthorizationHeader(authorizationHeader);
		Note note = getExistingNoteForUser(id, userId);
		note.setPinned(Boolean.TRUE.equals(pinNoteRequestDTO.getIsPinned()));
		return noteRepository.save(note);
	}

	@Transactional
	public void deleteNoteById(String authorizationHeader, Long id) {
		String userId = resolveUserIdFromAuthorizationHeader(authorizationHeader);
		Note note = getExistingNoteForUser(id, userId);
		noteRepository.delete(note);
	}

	private Note getExistingNoteForUser(Long id, String userId) {
		return noteRepository.findByIdAndUserId(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
	}

	private Note toEntity(NoteRequestDTO requestDTO, String userId) {
		return Note.builder()
				.title(requestDTO.getTitle())
				.content(requestDTO.getContent())
				.userId(userId)
				.isPinned(false)
				.build();
	}

	private String resolveUserIdFromAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new IllegalArgumentException("Authorization header is required");
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization header must use Bearer token");
		}

		String token = authorizationHeader.substring(7).trim();
		if (token.isBlank()) {
			throw new IllegalArgumentException("Bearer token is required");
		}

		return token;
	}
}