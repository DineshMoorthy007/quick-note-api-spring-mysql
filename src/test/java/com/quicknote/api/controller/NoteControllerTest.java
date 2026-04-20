package com.quicknote.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.quicknote.api.dto.NoteRequestDTO;
import com.quicknote.api.exception.GlobalExceptionHandler;
import com.quicknote.api.exception.ResourceNotFoundException;
import com.quicknote.api.model.Note;
import com.quicknote.api.service.NoteService;
import java.util.List;
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
class NoteControllerTest {

	@Mock
	private NoteService noteService;

	@InjectMocks
	private NoteController noteController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(noteController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void getNotesShouldReturn200AndList() throws Exception {
		List<Note> notes = List.of(
				Note.builder().id(1L).title("N1").content("C1").userId("user-1").isPinned(false).build(),
				Note.builder().id(2L).title("N2").content("C2").userId("user-1").isPinned(true).build());
		when(noteService.getAllNotes("Bearer user-1")).thenReturn(notes);

		mockMvc.perform(get("/api/notes").header("Authorization", "Bearer user-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].title").value("N1"));
	}

	@Test
	void createNoteShouldReturn201AndCreatedNote() throws Exception {
		Note created = Note.builder().id(10L).title("T").content("Body").userId("user-1").isPinned(false).build();
		when(noteService.createNote(eq("Bearer user-1"), any(NoteRequestDTO.class))).thenReturn(created);

		mockMvc.perform(post("/api/notes")
						.header("Authorization", "Bearer user-1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"T\",\"content\":\"Body\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.title").value("T"));
	}

	@Test
	void updateNoteShouldReturn200AndUpdatedNote() throws Exception {
		Note updated = Note.builder().id(7L).title("New title").content("New content").userId("user-1").build();
		when(noteService.updateNoteById(eq("Bearer user-1"), eq(7L), any(NoteRequestDTO.class))).thenReturn(updated);

		mockMvc.perform(put("/api/notes/7")
						.header("Authorization", "Bearer user-1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"New title\",\"content\":\"New content\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.content").value("New content"));
	}

	@Test
	void pinNoteShouldReturn200AndUpdatedPinState() throws Exception {
		Note updated = Note.builder().id(8L).title("T").content("C").userId("user-1").isPinned(true).build();
		when(noteService.pinNoteById(eq("Bearer user-1"), eq(8L), any())).thenReturn(updated);

		mockMvc.perform(put("/api/notes/8/pin")
						.header("Authorization", "Bearer user-1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"isPinned\":true}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(8))
				.andExpect(jsonPath("$.pinned").value(true));
	}

	@Test
	void deleteNoteShouldReturn200WithJsonBody() throws Exception {
		doNothing().when(noteService).deleteNoteById("Bearer user-1", 11L);

		mockMvc.perform(delete("/api/notes/11").header("Authorization", "Bearer user-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Note deleted successfully"))
				.andExpect(jsonPath("$.id").value(11));

		verify(noteService).deleteNoteById("Bearer user-1", 11L);
	}

	@Test
	void createNoteShouldReturn400ForInvalidPayload() throws Exception {
		mockMvc.perform(post("/api/notes")
						.header("Authorization", "Bearer user-1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"title\":\"\",\"content\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	void deleteNoteShouldReturn404WhenMissing() throws Exception {
		doThrow(new ResourceNotFoundException("Note not found with id: 404"))
				.when(noteService)
				.deleteNoteById("Bearer user-1", 404L);

		mockMvc.perform(delete("/api/notes/404").header("Authorization", "Bearer user-1"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void getNotesShouldReturn400WhenAuthorizationHeaderInvalid() throws Exception {
		when(noteService.getAllNotes(null)).thenThrow(new IllegalArgumentException("Authorization header is required"));

		mockMvc.perform(get("/api/notes"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}
}
