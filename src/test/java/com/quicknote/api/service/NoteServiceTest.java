package com.quicknote.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quicknote.api.dto.NoteRequestDTO;
import com.quicknote.api.dto.PinNoteRequestDTO;
import com.quicknote.api.exception.ResourceNotFoundException;
import com.quicknote.api.model.Note;
import com.quicknote.api.repository.NoteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

	private static final String AUTH_HEADER = "Bearer user-123";

	@Mock
	private NoteRepository noteRepository;

	@InjectMocks
	private NoteService noteService;

	private NoteRequestDTO noteRequestDTO;

	@BeforeEach
	void setUp() {
		noteRequestDTO = NoteRequestDTO.builder()
				.title("Test title")
				.content("Test content")
				.build();
	}

	@Test
	void createNoteShouldMapDtoAndSave() {
		when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Note created = noteService.createNote(AUTH_HEADER, noteRequestDTO);

		ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
		verify(noteRepository).save(captor.capture());
		Note saved = captor.getValue();

		assertEquals("Test title", saved.getTitle());
		assertEquals("Test content", saved.getContent());
		assertEquals("user-123", saved.getUserId());
		assertEquals(false, saved.isPinned());
		assertEquals("user-123", created.getUserId());
	}

	@Test
	void getAllNotesShouldUseUserIdFromBearerToken() {
		List<Note> expected = List.of(Note.builder().id(1L).title("A").content("B").userId("user-123").build());
		when(noteRepository.findByUserId("user-123")).thenReturn(expected);

		List<Note> notes = noteService.getAllNotes(AUTH_HEADER);

		assertEquals(1, notes.size());
		verify(noteRepository).findByUserId("user-123");
	}

	@Test
	void updateNoteByIdShouldUpdateTitleAndContent() {
		Note existing = Note.builder().id(9L).title("Old").content("Old content").userId("user-123").build();
		when(noteRepository.findByIdAndUserId(9L, "user-123")).thenReturn(Optional.of(existing));
		when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Note updated = noteService.updateNoteById(AUTH_HEADER, 9L, noteRequestDTO);

		assertEquals("Test title", updated.getTitle());
		assertEquals("Test content", updated.getContent());
		verify(noteRepository).save(existing);
	}

	@Test
	void pinNoteByIdShouldUpdatePinFlag() {
		Note existing = Note.builder().id(10L).title("X").content("Y").userId("user-123").isPinned(false).build();
		PinNoteRequestDTO request = PinNoteRequestDTO.builder().isPinned(true).build();
		when(noteRepository.findByIdAndUserId(10L, "user-123")).thenReturn(Optional.of(existing));
		when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Note pinned = noteService.pinNoteById(AUTH_HEADER, 10L, request);

		assertEquals(true, pinned.isPinned());
		verify(noteRepository).save(existing);
	}

	@Test
	void pinNoteByIdShouldSetFalseWhenValueIsNull() {
		Note existing = Note.builder().id(10L).title("X").content("Y").userId("user-123").isPinned(true).build();
		PinNoteRequestDTO request = PinNoteRequestDTO.builder().isPinned(null).build();
		when(noteRepository.findByIdAndUserId(10L, "user-123")).thenReturn(Optional.of(existing));
		when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Note updated = noteService.pinNoteById(AUTH_HEADER, 10L, request);

		assertEquals(false, updated.isPinned());
		verify(noteRepository).save(existing);
	}

	@Test
	void deleteNoteByIdShouldDeleteExistingOwnedNote() {
		Note existing = Note.builder().id(22L).title("T").content("C").userId("user-123").build();
		when(noteRepository.findByIdAndUserId(22L, "user-123")).thenReturn(Optional.of(existing));

		noteService.deleteNoteById(AUTH_HEADER, 22L);

		verify(noteRepository).delete(existing);
	}

	@Test
	void deleteNoteByIdShouldThrowWhenNotFound() {
		when(noteRepository.findByIdAndUserId(99L, "user-123")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> noteService.deleteNoteById(AUTH_HEADER, 99L));
		verify(noteRepository, never()).delete(any());
	}

	@Test
	void getAllNotesShouldThrowWhenAuthorizationHeaderMissing() {
		assertThrows(IllegalArgumentException.class, () -> noteService.getAllNotes(null));
	}

	@Test
	void getAllNotesShouldThrowWhenAuthorizationHeaderBlank() {
		assertThrows(IllegalArgumentException.class, () -> noteService.getAllNotes("   "));
	}

	@Test
	void createNoteShouldThrowWhenAuthorizationHeaderIsNotBearer() {
		assertThrows(
				IllegalArgumentException.class,
				() -> noteService.createNote("Token user-123", noteRequestDTO));
	}

	@Test
	void createNoteShouldThrowWhenBearerTokenIsBlank() {
		assertThrows(
				IllegalArgumentException.class,
				() -> noteService.createNote("Bearer   ", noteRequestDTO));
	}
}