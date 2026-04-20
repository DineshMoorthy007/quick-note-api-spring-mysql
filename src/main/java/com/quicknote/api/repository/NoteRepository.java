package com.quicknote.api.repository;

import com.quicknote.api.model.Note;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

	List<Note> findByUserId(String userId);

	java.util.Optional<Note> findByIdAndUserId(Long id, String userId);
}