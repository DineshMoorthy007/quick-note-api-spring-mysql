package com.quicknote.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinNoteRequestDTO {

	@NotNull(message = "isPinned is required")
	private Boolean isPinned;
}