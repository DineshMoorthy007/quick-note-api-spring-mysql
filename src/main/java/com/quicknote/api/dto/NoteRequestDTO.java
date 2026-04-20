package com.quicknote.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteRequestDTO {

	@NotBlank(message = "title is required")
	private String title;

	@NotBlank(message = "content is required")
	private String content;
}